package com.pinyougou.sellergoods.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.sellergoods.service.SeckillOrderService;

import entity.PageResult;


@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {
	@Autowired
	private TbSeckillOrderMapper  tbSeckillOrderMapper;

	@Override
	public PageResult findPage(int pageNo, int pageSize) {
		PageHelper.startPage(pageNo, pageSize);
		Page<TbSeckillOrder> page=(Page<TbSeckillOrder>) tbSeckillOrderMapper.selectByExample(null);
		return new PageResult(page.getTotal(),page.getResult());
	}

	
}
