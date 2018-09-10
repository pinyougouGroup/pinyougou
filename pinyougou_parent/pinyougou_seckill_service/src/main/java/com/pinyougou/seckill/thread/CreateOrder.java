package com.pinyougou.seckill.thread;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;

import entity.UserIdAndSeckillGoodsId;
import util.IdWorker;

public class CreateOrder implements Runnable {

	@Autowired
	private RedisTemplate  redisTemplate;
	@Autowired
	private IdWorker  idWorker;
	@Autowired
	private TbSeckillOrderMapper  seckillOrderMapper;
	@Autowired
	private TbSeckillGoodsMapper  seckillGoodsMapper;
	
	@Override
	public void run() {
		UserIdAndSeckillGoodsId userIdAndSeckillGoodsId = (entity.UserIdAndSeckillGoodsId) redisTemplate.boundListOps("seckill_order_num").rightPop();
		Long id = userIdAndSeckillGoodsId.getId();
		String userId = userIdAndSeckillGoodsId.getUserId();
		
		TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckill_goods").get(id);
		 TbSeckillOrder seckillOrder = new TbSeckillOrder();
		 seckillOrder.setCreateTime(new Date());
		 seckillOrder.setId(idWorker.nextId());
		 seckillOrder.setMoney(seckillGoods.getCostPrice());
		 seckillOrder.setSeckillId(id);
		 seckillOrder.setSellerId(seckillGoods.getSellerId());
		 seckillOrder.setStatus("0");
		 seckillOrder.setUserId(userId);
		 seckillOrderMapper.insert(seckillOrder);
//		 减库存
		 seckillGoods.setStockCount(seckillGoods.getStockCount()-1);
		 if(seckillGoods.getStockCount()==0) {
//			 库存为0把商品从redis中移除
			 redisTemplate.boundHashOps("seckill_goods").delete(id);
//			 更新mysql数据库
			 seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
		 }else {
			 redisTemplate.boundHashOps("seckill_goods").put(id, seckillGoods); 
		 }
		 
		 
		 redisTemplate.boundHashOps("seckill_order").put(userId, seckillOrder);//待支付的秒杀的订单

		 System.out.println("执行了多线程中的方法");
	}

}
