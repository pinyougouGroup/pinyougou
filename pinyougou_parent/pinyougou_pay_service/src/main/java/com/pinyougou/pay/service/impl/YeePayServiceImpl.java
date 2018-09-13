package com.pinyougou.pay.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.pay.service.YeePayService;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbPayLog;

import util.PaymentUtil;

@Service
@Transactional
public class YeePayServiceImpl implements YeePayService {

	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	private TbOrderMapper orderMapper;

	@Override
	public Map findOrderByUserId(String userId) {
		TbPayLog payLog = (TbPayLog) redisTemplate.boundHashOps("payLog").get(userId);
		String out_trade_no = payLog.getOutTradeNo();
		Map<String, String> resultMap = new HashMap<>();
	    resultMap.put("out_trade_no", out_trade_no+"");
	    resultMap.put("total_fee", "1");
		return resultMap;
	}
	
	@Value("${p1_MerId}")
	private String p1_MerId;
	@Value("${responseURL}")
	private String p8_Url;
	@Value("${keyValue}")
	private String keyValue;

	@Override
	public String confirmOrder(String pd_FrpId,String userId) {
		// 3.拼接易宝需要的参数,完成重定向的动作
		// 组织发送支付公司需要哪些数据
		//String pd_FrpId = request.getParameter("pd_FrpId");
		String p0_Cmd = "Buy";
		//String p1_MerId = ResourceBundle.getBundle("merchantInfo").getString("p1_MerId");
		
		TbPayLog payLog = (TbPayLog) redisTemplate.boundHashOps("payLog").get(userId);
		String out_trade_no = payLog.getOutTradeNo();
		
		String p2_Order = out_trade_no;
		String p3_Amt = "0.01";
		String p4_Cur = "CNY";
		String p5_Pid = "";
		String p6_Pcat = "";
		String p7_Pdesc = "";
		// 支付成功回调地址 ---- 第三方支付公司会访问、用户访问
		// 第三方支付可以访问网址
		//String p8_Url = ResourceBundle.getBundle("merchantInfo").getString("responseURL");
		String p9_SAF = "";
		String pa_MP = "";
		String pr_NeedResponse = "1";
		// 加密hmac 需要密钥
		//String keyValue = ResourceBundle.getBundle("merchantInfo").getString("keyValue");
		String hmac = PaymentUtil.buildHmac(p0_Cmd, p1_MerId, p2_Order, p3_Amt, p4_Cur, p5_Pid, p6_Pcat, p7_Pdesc,
				p8_Url, p9_SAF, pa_MP, pd_FrpId, pr_NeedResponse, keyValue);

		// 发送给第三方
		StringBuffer sb = new StringBuffer("");
		sb.append("p0_Cmd=").append(p0_Cmd).append("&");
		sb.append("p1_MerId=").append(p1_MerId).append("&");
		sb.append("p2_Order=").append(p2_Order).append("&");
		sb.append("p3_Amt=").append(p3_Amt).append("&");
		sb.append("p4_Cur=").append(p4_Cur).append("&");
		sb.append("p5_Pid=").append(p5_Pid).append("&");
		sb.append("p6_Pcat=").append(p6_Pcat).append("&");
		sb.append("p7_Pdesc=").append(p7_Pdesc).append("&");
		sb.append("p8_Url=").append(p8_Url).append("&");
		sb.append("p9_SAF=").append(p9_SAF).append("&");
		sb.append("pa_MP=").append(pa_MP).append("&");
		sb.append("pd_FrpId=").append(pd_FrpId).append("&");
		sb.append("pr_NeedResponse=").append(pr_NeedResponse).append("&");
		sb.append("hmac=").append(hmac);
		
		return sb.toString();
	}

	@Autowired
	private TbPayLogMapper payLogMapper;
	
	@Override
	public String callback(String p1_MerId, String r0_Cmd, String r1_Code, String r2_TrxId, String r3_Amt,
			String r4_Cur, String r5_Pid, String r6_Order, String r7_Uid, String r8_MP, String r9_BType,
			String rb_BankId, String ro_BankOrderId, String rp_PayDate, String rq_CardNo, String ru_Trxtime,
			String hmac) {
		
		//String keyValue = ResourceBundle.getBundle("merchantInfo").getString("keyValue");

		// 自己对上面数据进行加密 --- 比较支付公司发过来hamc
		boolean isValid = PaymentUtil.verifyCallback(hmac, p1_MerId, r0_Cmd,
				r1_Code, r2_TrxId, r3_Amt, r4_Cur, r5_Pid, r6_Order, r7_Uid,
				r8_MP, r9_BType, keyValue);
		if (isValid) {
			// 响应数据有效,判断支付结果是否成功了
			if (r1_Code.equals("1")) {
				//成功了
				//修改订单状态
				TbPayLog payLog = payLogMapper.selectByPrimaryKey(r6_Order);
				payLog.setPayTime(new Date());
				payLog.setTradeState("1");
				payLogMapper.updateByPrimaryKey(payLog);
//				修改订单 1,2,3,4
				String[] orderIds = payLog.getOrderList().split(",");
				for (String orderId : orderIds) {
					TbOrder order = orderMapper.selectByPrimaryKey(Long.parseLong(orderId));
					order.setPaymentTime(new Date());
					order.setStatus("2");
					order.setUpdateTime(new Date());
					orderMapper.updateByPrimaryKey(order);
				}
			} else {
				//响应数据有效,但支付结果是失败的
			}
		} else {
			// 数据无效
			System.out.println("数据被篡改！");
		}
		System.out.println(r6_Order);
		return "http://pay.pinyougou.com/yeePaysuccess.html#?r3_Amt="+r3_Amt+"&r6_Order="+r6_Order;
		
	}

}
