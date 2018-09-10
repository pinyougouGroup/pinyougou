package com.pinyougou.seckill.service;

import java.util.List;

import com.pinyougou.pojo.TbSeckillGoods;

public interface SeckillService {

	List<TbSeckillGoods> findAllFromRedis();

	TbSeckillGoods findFromRedis(Long id);

	void saveSeckillOrder(Long id, String userId);

}
