package com.pinyougou.sellergoods.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillGoodsExample;
import com.pinyougou.sellergoods.service.SeckillService;

import entity.PageResult;
import util.IdWorker;

@Service
public class SeckillServiceImpl implements SeckillService {
	@Autowired
	private TbSeckillGoodsMapper tbSeckillGoodsMapper;
	
	@Autowired
	private IdWorker idWorker;

	@Override
	public List<TbSeckillGoods> findAll() {

		return tbSeckillGoodsMapper.selectByExample(null);
	}

	@Override
	public void add(TbSeckillGoods tbSeckillGoods) {
		tbSeckillGoods.setStatus("0");
		tbSeckillGoods.setCreateTime(new Date());
		tbSeckillGoods.setCheckTime(new Date());
		long goodId = idWorker.nextId();
		long itemId = idWorker.nextId();
		tbSeckillGoods.setGoodsId(goodId);
		tbSeckillGoods.setItemId(itemId);
		tbSeckillGoodsMapper.insert(tbSeckillGoods);
		
	}

	@Override
	public PageResult search(TbSeckillGoods tbSeckillGoods, int pageNum, int pageSize) {
	  PageHelper.startPage(pageNum, pageSize);
		TbSeckillGoodsExample example=new TbSeckillGoodsExample();
		if(StringUtils.isNotBlank(tbSeckillGoods.getStatus())) {
			example.createCriteria().andStatusEqualTo("0");
		}
		Page<TbSeckillGoods> page=(Page<TbSeckillGoods>) tbSeckillGoodsMapper.selectByExample(example);
		return new PageResult(page.getTotal(),page.getResult());
	}

	@Override
	public TbSeckillGoods findOne(Long id) {
		return tbSeckillGoodsMapper.selectByPrimaryKey(id);
	}

	@Override
	public void updateStatus(String status, Long id) {
		TbSeckillGoods seckillGoods = tbSeckillGoodsMapper.selectByPrimaryKey(id);
		seckillGoods.setStatus(status);
		tbSeckillGoodsMapper.updateByPrimaryKey(seckillGoods);
		
	}

	

}
