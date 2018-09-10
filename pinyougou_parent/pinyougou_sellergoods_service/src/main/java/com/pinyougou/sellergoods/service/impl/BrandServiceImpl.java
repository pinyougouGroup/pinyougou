package com.pinyougou.sellergoods.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.pojo.TbBrandExample.Criteria;
import com.pinyougou.sellergoods.service.BrandService;

import entity.PageResult;

@Service
@Transactional
public class BrandServiceImpl implements BrandService {
	@Autowired
	private TbBrandMapper brandMapper;
	
	@Override
	public List<TbBrand> findAll() {
		return brandMapper.selectByExample(null);
	}

	@Override
	public PageResult findPage(int pageNo, int pageSize) {
//		使用pageHelper 分页
		PageHelper.startPage(pageNo, pageSize);
//		使用Page强转结果
		Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(null);
		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void add(TbBrand brand) {
		brandMapper.insert(brand);
		
	}

	@Override
	public TbBrand findOne(Long id) {
		// TODO Auto-generated method stub
		return brandMapper.selectByPrimaryKey(id);
	}

	@Override
	public void update(TbBrand brand) {
		brandMapper.updateByPrimaryKey(brand);
		
	}

	@Override
	public void dele(Long[] ids) {
		for (Long long1 : ids) {
			brandMapper.deleteByPrimaryKey(long1);
		}
		
		
	}

	@Override
	public PageResult findPage(int pageNo, int pageSize, TbBrand brand) {
//		使用pageHelper 分页
		PageHelper.startPage(pageNo, pageSize);
		TbBrandExample example = new TbBrandExample(); 
		Criteria criteria = example.createCriteria();
//		brand.getFirstChar()
		if(StringUtils.isNotBlank(brand.getName())) {
			criteria.andNameLike("%"+brand.getName()+"%");
		}
		if(StringUtils.isNotBlank(brand.getFirstChar())) {
			criteria.andFirstCharEqualTo(brand.getFirstChar());
		}
		//		使用Page强转结果
		Page<TbBrand> page = (Page<TbBrand>)brandMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

//	添加模板数据时需要的品牌数据 要求返回的格式：[{id:1,text:"SSS"},{}...]
	@Override
	public List<Map> findBrandList() {
		 
		return brandMapper.findBrandList();
	}

}
