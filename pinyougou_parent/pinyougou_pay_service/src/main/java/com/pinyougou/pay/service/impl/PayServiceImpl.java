package com.pinyougou.pay.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.pay.service.PayService;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbPayLog;

import util.HttpClient;
import util.IdWorker;
@Service
@Transactional
public class PayServiceImpl implements PayService {

	@Value("${appid}")
	private String appid;
	@Value("${partner}")
	private String partner;  //商户号
	@Value("${partnerkey}")
	private String partnerkey;
	@Value("${notifyurl}")
	private String notifyurl;
	
	@Autowired
	private IdWorker  idWorker;
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	@Autowired
	private TbPayLogMapper payLogMapper;
	
	@Autowired
	private TbOrderMapper orderMapper;
	

	@Override
	public Map createNative(String userId) throws Exception {
//		调用微信统一下单接口
		HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
		Map<String,String> paramMap = new HashMap<String,String>();
//		long out_trade_no = idWorker.nextId();
//		真实的订单号
		TbPayLog payLog = (TbPayLog) redisTemplate.boundHashOps("payLog").get(userId);
		String out_trade_no = payLog.getOutTradeNo();
		
//		公众账号ID	appid	是	String(32)	wxd678efh567hg6787	微信支付分配的公众账号ID（企业号corpid即为此appId）
		paramMap.put("appid", appid);
		//		商户号	mch_id	是	String(32)	1230000109	微信支付分配的商户号
		paramMap.put("mch_id", partner);
		//		随机字符串	nonce_str	是	String(32)	5K8264ILTKCH16CQ2502SI8ZNMTM67VS	随机字符串，长度要求在32位以内。推荐随机数生成算法
		String generateNonceStr = WXPayUtil.generateNonceStr();
		paramMap.put("nonce_str", generateNonceStr);
//		商品描述	body	是	String(128)	腾讯充值中心-QQ会员充值	商品简单描述，该字段请按照规范传递，具体请见参数规定
		paramMap.put("body", "品优购支付");
//		商户订单号	out_trade_no	是	String(32)	20150806125346	商户系统内部订单号
		paramMap.put("out_trade_no", out_trade_no+"");
//		标价金额	total_fee	是	Int	88	订单总金额，单位：分
		paramMap.put("total_fee","1");
//		通知地址	notify_url	是	String(256)	http://www.weixin.qq.com/wxpay/pay.php	异步接收微信支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数。
		paramMap.put("notify_url",notifyurl);
		//		交易类型	trade_type	是	String(16)	JSAPI	JSAPI 公众号支付	NATIVE 扫码支付		APP APP支付
		paramMap.put("trade_type","NATIVE");
		//		签名	sign	是	String(32)	C380BEC2BFD727A4B6845133519F3AD6	通过签名算法计算得出的签名值，详见签名生成算法
	    String paramXml = WXPayUtil.generateSignedXml(paramMap,partnerkey);
	    httpClient.setXmlParam(paramXml);
	    httpClient.post();
	    String content = httpClient.getContent();
	    Map<String, String> resultMap = WXPayUtil.xmlToMap(content);
	    resultMap.put("out_trade_no", out_trade_no+"");
	    resultMap.put("total_fee", "1");
	    
	    return resultMap;
	    
	}



	@Override
	public Map  queryOrder(String out_trade_no) throws Exception {
//		调用微信查询订单的接口
		HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
		Map<String,String> paramMap = new HashMap<String,String>();
//		公众账号ID	appid	是	String(32)	wxd678efh567hg6787	微信支付分配的公众账号ID（企业号corpid即为此appId）
		paramMap.put("appid", appid);
		//		商户号	mch_id	是	String(32)	1230000109	微信支付分配的商户号
		paramMap.put("mch_id", partner);
		//		随机字符串	nonce_str	是	String(32)	5K8264ILTKCH16CQ2502SI8ZNMTM67VS	随机字符串，长度要求在32位以内。推荐随机数生成算法
		String generateNonceStr = WXPayUtil.generateNonceStr();
		paramMap.put("nonce_str", generateNonceStr);
//		商户订单号
		paramMap.put("out_trade_no", out_trade_no+"");
		//签名	sign	是	String(32)	C380BEC2BFD727A4B6845133519F3AD6	通过签名算法计算得出的签名值，详见签名生成算法
	    String paramXml = WXPayUtil.generateSignedXml(paramMap,partnerkey);
	    httpClient.setXmlParam(paramXml);
	    httpClient.post();
	    String content = httpClient.getContent();
	    Map<String, String> resultMap = WXPayUtil.xmlToMap(content);
	    
	    return resultMap;
		
	}



	@Override
	public void updatePayLog(String transaction_id,String userId) {
//		修改payLog
		TbPayLog payLog = (TbPayLog) redisTemplate.boundHashOps("payLog").get(userId);
		payLog.setPayTime(new Date());
		payLog.setTradeState("1");
		payLog.setTransactionId(transaction_id);
		payLogMapper.updateByPrimaryKey(payLog);
//		修改订单 1,2,3,4
		String[] orderIds = payLog.getOrderList().split(",");
		for (String orderId : orderIds) {
			TbOrder order = orderMapper.selectByPrimaryKey(Long.parseLong(orderId));
			order.setPaymentTime(new Date());
			order.setStatus("2");
			order.setUpdateTime(new Date());
			orderMapper.updateByPrimaryKey(order);
		}
		
	}
	
	
}
