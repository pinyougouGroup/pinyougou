package com.pinyougou.seckill.task;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillGoodsExample;

@Component
public class SeckillTask {

	@Autowired
	private TbSeckillGoodsMapper seckillGoodsMapper;
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	@Scheduled(cron="20 1 18 6 9 ?")
	public  void saveToRedis() {
//		 从mysql中获取秒杀的商品
//		 秒杀商品的条件：
//		 1、时间范围
//		startTime <= date() <=  endTime
//		 2、剩余数量大于0
//		 3、审核通过
		
		TbSeckillGoodsExample example = new TbSeckillGoodsExample();
		example.createCriteria()
		       .andStockCountGreaterThan(0) //存款数大于0
		       .andStatusEqualTo("1")  //审核通过的
		       .andStartTimeLessThanOrEqualTo(new Date())  //开始时间小于当前时间的
		       .andEndTimeGreaterThanOrEqualTo(new Date()); //结束时间大于当前时间的
		List<TbSeckillGoods> list = seckillGoodsMapper.selectByExample(example );
		
//		放入到redis中
		for (TbSeckillGoods tbSeckillGoods : list) {
			Long id = tbSeckillGoods.getId();
			redisTemplate.boundHashOps("seckill_goods").put(id, tbSeckillGoods);
			for (int i = 0; i < tbSeckillGoods.getStockCount(); i++) {
//				每个商品的id压栈 有多少库存就压栈多少次
				redisTemplate.boundListOps("seckill_goods_num"+id).leftPush(id);
			}
			
		}
		System.out.println("已存放秒杀商品");
		
	}
}
