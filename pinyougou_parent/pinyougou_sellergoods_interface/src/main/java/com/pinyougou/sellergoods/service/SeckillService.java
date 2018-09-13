package com.pinyougou.sellergoods.service;

import java.util.List;

import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbSeckillGoods;

import entity.PageResult;

public interface SeckillService {

	List<TbSeckillGoods> findAll();

	void add(TbSeckillGoods tbSeckillGoods);

	PageResult search(TbSeckillGoods tbSeckillGoods, int pageNum, int pageSize);

	TbSeckillGoods findOne(Long id);

	void updateStatus(String status, Long id);

	

	

}
