package com.pinyougou.sellergoods.service;

import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbBrand;

import entity.PageResult;

public interface BrandService {
	
	public List<TbBrand> findAll();

	public PageResult findPage(int pageNo, int pageSize);

	public void add(TbBrand brand);

	public TbBrand findOne(Long id);

	public void update(TbBrand brand);

	public void dele(Long[] ids);

	public PageResult findPage(int pageNo, int pageSize, TbBrand brand);

	public List<Map> findBrandList();
	
}
