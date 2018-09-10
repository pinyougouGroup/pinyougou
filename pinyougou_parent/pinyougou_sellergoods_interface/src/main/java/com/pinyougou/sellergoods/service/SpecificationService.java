package com.pinyougou.sellergoods.service;

import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbSpecification;

import entity.PageResult;
import groupEntity.Specification;

public interface SpecificationService {
	
	public List<TbSpecification> findAll();

	public PageResult findPage(int pageNo, int pageSize);

	public void add(Specification specification);

	public Specification findOne(Long id);

	public void update(Specification specification);

	public void dele(Long[] ids);

	public PageResult findPage(int pageNo, int pageSize, TbSpecification specification);

	public List<Map> findSpecList();
	
}
