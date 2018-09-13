package com.pinyougou.seckill.thread;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbPayLog;
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
	
	@Autowired
	private TbPayLogMapper payLogMapper;
	
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

		//还需要保存到支付日志表一份数据
			TbPayLog  tbPayLog= new TbPayLog();
			tbPayLog.setCreateTime(new Date());
			tbPayLog.setOrderList(id+"");
			tbPayLog.setPayType("2");//1:货到付款 2:微信支付 3:支付宝支付
			tbPayLog.setTotalFee(seckillOrder.getMoney().longValue());
			tbPayLog.setOutTradeNo(idWorker.nextId()+"");
			tbPayLog.setUserId(seckillOrder.getUserId());
			tbPayLog.setTradeState("0");//0表示未支付,1表示已支付
			//存储到数据库中
			payLogMapper.insert(tbPayLog);
			System.out.println(tbPayLog.getOutTradeNo());
			//把支付日志放到redis中
			redisTemplate.boundHashOps("payLog").put(userId, tbPayLog);
			System.out.println("商品信息已经写入进去");
	}

}
