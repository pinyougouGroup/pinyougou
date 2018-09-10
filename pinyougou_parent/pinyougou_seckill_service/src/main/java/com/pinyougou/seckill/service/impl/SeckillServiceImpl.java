package com.pinyougou.seckill.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillService;
import com.pinyougou.seckill.thread.CreateOrder;

import entity.UserIdAndSeckillGoodsId;
import util.IdWorker;

@Service
public class SeckillServiceImpl implements SeckillService{

	@Autowired
	private RedisTemplate  redisTemplate;
	@Autowired
	private IdWorker  idWorker;
	@Autowired
	private TbSeckillOrderMapper  seckillOrderMapper;
	@Autowired
	private TbSeckillGoodsMapper  seckillGoodsMapper;
	@Autowired
	private ThreadPoolTaskExecutor executor;
	@Autowired
	private CreateOrder createOrder;
	@Override
	public List<TbSeckillGoods> findAllFromRedis() {
		return redisTemplate.boundHashOps("seckill_goods").values();
	}

	@Override
	public TbSeckillGoods findFromRedis(Long id) {
		return  (TbSeckillGoods) redisTemplate.boundHashOps("seckill_goods").get(id);
	}

	@Override
	public void saveSeckillOrder(Long id, String userId) {
//		 `id` bigint(20) NOT NULL COMMENT '主键',
//		 `money` decimal(10,2) DEFAULT NULL COMMENT '支付金额',
//		 `user_id` varchar(50) DEFAULT NULL COMMENT '用户',
//		 `seller_id` varchar(50) DEFAULT NULL COMMENT '商家',
//		 `create_time` datetime DEFAULT NULL COMMENT '创建时间', 
//		 `status` varchar(1) DEFAULT NULL COMMENT '状态',
		
		 Object object = redisTemplate.boundHashOps("seckill_order").get(userId);//待支付的秒杀的订单
		 if(object!=null) {// 意味着此人有未付款的秒杀订单，不让重复购买
			 throw new RuntimeException("您有未支付的订单");
		 }
		
		Object rightPop = redisTemplate.boundListOps("seckill_goods_num"+id).rightPop(); // rightPop从右侧出栈
		if(rightPop==null) { //代表商品id已全部出栈
			throw new RuntimeException("已售罄");
		}
		
		redisTemplate.boundListOps("seckill_order_num").leftPush(new UserIdAndSeckillGoodsId(userId, id));
		
		
		executor.execute(createOrder);
		
		
//		 if(seckillGoods==null) {
//			throw new RuntimeException("已售罄");
//		 }
		

		 
	}
}
