package com.pinyougou.order.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbPayLog;

import groupEntity.Cart;
import util.IdWorker;
@Service
public class OrderServiceImpl implements OrderService {

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

}
