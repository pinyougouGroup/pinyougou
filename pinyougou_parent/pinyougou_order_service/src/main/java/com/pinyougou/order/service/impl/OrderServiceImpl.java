package com.pinyougou.order.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderExample;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbPayLog;

import groupEntity.Cart;
import util.HttpClient;
import util.IdWorker;
@Service
public class OrderServiceImpl implements OrderService {
	
	
	@Value("${appid}")
	private String appid;
	
	@Value("${partner}")
	private String partner;//商户号
	
	@Value("${partnerkey}")
	private String partnerkey;
	
	@Value("${notifyurl}")
	private String notifyurl;
	
	
	@Autowired
	private IdWorker idWorker;
	
	@Autowired
	private TbOrderMapper orderMapper;
	@Autowired
	private TbOrderItemMapper orderItemMapper;
	
	@Autowired
	private TbPayLogMapper payLogMapper;
	
	@Autowired
	private RedisTemplate redisTemplate;
	@Override
	public void save(TbOrder order) {
//		 `user_id` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '用户id',	
//		 `order_id` bigint(20) NOT NULL COMMENT '订单id',
		String userId = order.getUserId();
//		查询购物车数据
		List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(userId);
		String orderList="";
		double totalFee = 0.00;
		for (Cart cart : cartList) {
//			 `payment_type` varchar(1) COLLATE utf8_bin DEFAULT NULL COMMENT '支付类型，1、在线支付，2、货到付款',
//			  `receiver_area_name` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '收货人地区名称(省，市，县)街道',
//			  `receiver_mobile` varchar(12) COLLATE utf8_bin DEFAULT NULL COMMENT '收货人手机',
//			  `receiver` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '收货人',
//			  `source_type` varchar(1) COLLATE utf8_bin DEFAULT NULL COMMENT '订单来源：1:app端，2：pc端，3：M端，4：微信端，5：手机qq端',
//
			TbOrder tbOrder = new TbOrder();
			tbOrder.setPaymentType(order.getPaymentType());
			tbOrder.setReceiverAreaName(order.getReceiverAreaName());
			tbOrder.setReceiverMobile(order.getReceiverMobile());
			tbOrder.setReceiver(order.getReceiver());
			tbOrder.setSourceType(order.getSourceType());
			
//			 `order_id` bigint(20) NOT NULL COMMENT '订单id',
			long orderId = idWorker.nextId();
			tbOrder.setOrderId(orderId);
			orderList+=orderId+",";
			double payment =0.00;
			for(TbOrderItem orderItem:cart.getOrderItemList()) {
				payment+=orderItem.getTotalFee().doubleValue();
//				 保存订单项
//				   `id` bigint(20) NOT NULL,
//				   `order_id` bigint(20) NOT NULL COMMENT '订单id',
				orderItem.setId(idWorker.nextId());
				orderItem.setOrderId(orderId);
				orderItemMapper.insert(orderItem);
			}
			totalFee+=payment;
//			 `payment` decimal(20,2) DEFAULT NULL COMMENT '实付金额。精确到2位小数;单位:元。如:200.07，表示:200元7分',
//			当前订单的实付金额
			tbOrder.setPayment(new BigDecimal(payment));
//			 `status` varchar(1) COLLATE utf8_bin DEFAULT NULL COMMENT '状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭,7、待评价',
			tbOrder.setStatus("1");
// 			`create_time` datetime DEFAULT NULL COMMENT '订单创建时间',
//			 `update_time` datetime DEFAULT NULL COMMENT '订单更新时间',
			tbOrder.setCreateTime(new Date());
			tbOrder.setUpdateTime(new Date());
			
//			 `user_id` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '用户id',
			tbOrder.setUserId(userId);
//			  `buyer_rate` varchar(2) COLLATE utf8_bin DEFAULT NULL COMMENT '买家是否已经评价', 0未评价  1已评价
			tbOrder.setBuyerRate("0");
//			`seller_id` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '商家ID',
			tbOrder.setSellerId(cart.getSellerId());
			
			orderMapper.insert(tbOrder);
		}
		
//		添加一条支付日志数据
		TbPayLog payLog = new TbPayLog();
		payLog.setCreateTime(new Date());
		payLog.setOrderList(orderList.substring(0, orderList.length()-1)); //去除最后一个多余的逗号
		payLog.setOutTradeNo(idWorker.nextId()+"");
		payLog.setPayType(order.getPaymentType());
		payLog.setTotalFee((long) (totalFee*100));
		payLog.setTradeState("0");
		payLog.setUserId(userId);
		payLogMapper.insert(payLog);
		
		redisTemplate.boundHashOps("payLog").put(userId, payLog);
//		清空当前用户购物车
		redisTemplate.boundHashOps("cartList").delete(userId);
	}
	
	@Override
	public void clearOrder() throws Exception {
		
		//下订单后,开始查询数据库,拿到订单状态为0的订单
		TbOrderExample example=new TbOrderExample();
		example.createCriteria().andStatusEqualTo("1");
		List<TbOrder> orderList = orderMapper.selectByExample(example);
		//遍历得到每一个未支付的订单
		for (TbOrder tbOrder : orderList) {
			long creatTime = tbOrder.getCreateTime().getTime();
			long nowTime = new Date().getTime();
			if((nowTime-creatTime)>=1*60*1000) {
				//调用微信关闭订单接口
				HttpClient httpClient=new HttpClient("https://api.mch.weixin.qq.com/pay/closeorder");
				Map<String, String> map=new HashMap<String, String>();
//				公众账号ID	appid	是	String(32)	wx8888888888888888	微信分配的公众账号ID（企业号corpid即为此appId）
				map.put("appid", appid);
//				商户号	mch_id	是	String(32)	1900000109	微信支付分配的商户号
				map.put("mch_id", partner);
				//商户订单号	out_trade_no	是	String(32)	1217752501201407033233368018	商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*@ ，且在同一个商户号下唯一。
				//获取支付日志表 pagLog
				TbPayLog payLog = (TbPayLog) redisTemplate.boundHashOps("payLog").get(tbOrder.getUserId());
				if(payLog!=null) {
					map.put("out_trade_no", payLog.getOutTradeNo());
				}
				//随机字符串	nonce_str	是	String(32)	5K8264ILTKCH16CQ2502SI8ZNMTM67VS	随机字符串，不长于32位。推荐随机数生成算法
				String nonceStr = WXPayUtil.generateNonceStr();
				map.put("nonce_str", nonceStr);
				//签名	sign	是	String(32)	C380BEC2BFD727A4B6845133519F3AD6	签名，详见签名生成算法
				String generateSignedXml = WXPayUtil.generateSignedXml(map, partnerkey);
				//调用远程服务
				httpClient.setXmlParam(generateSignedXml);
				httpClient.post();
				//更改数据库信息
				tbOrder.setStatus("6");//6表示订单已取消
				tbOrder.setUpdateTime(new Date());
				tbOrder.setExpire(new Date());
				orderMapper.updateByPrimaryKey(tbOrder);
				System.out.println("订单已关闭"+new Date());
			}
		}
			
	}
	
	
}
