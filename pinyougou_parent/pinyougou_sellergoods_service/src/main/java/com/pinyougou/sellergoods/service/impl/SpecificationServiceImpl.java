package com.pinyougou.sellergoods.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationExample;
import com.pinyougou.pojo.TbSpecificationExample.Criteria;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.sellergoods.service.SpecificationService;

import entity.PageResult;
import groupEntity.Specification;

@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService {
	@Autowired
	private TbSpecificationMapper specificationMapper;
	
	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;
	
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}

	@Override
	public PageResult findPage(int pageNo, int pageSize) {
//		使用pageHelper 分页
		PageHelper.startPage(pageNo, pageSize);
//		使用Page强转结果
		Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper.selectByExample(null);
		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void add(Specification specification) {
		
		TbSpecification tbSpecification = specification.getTbSpecification();
		specificationMapper.insert(tbSpecification);
		
		List<TbSpecificationOption> tbSpecificationOptionList = specification.getTbSpecificationOptionList();
		for (TbSpecificationOption tbSpecificationOption : tbSpecificationOptionList) {
			tbSpecificationOption.setSpecId(tbSpecification.getId());
			specificationOptionMapper.insert(tbSpecificationOption);
		}
		
	}

	@Override
	public Specification findOne(Long id) {
		Specification specification = new Specification();
		TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
		specification.setTbSpecification(tbSpecification);
		
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		example.createCriteria().andSpecIdEqualTo(id);
		//		select * from tb_specification_option where spec_id=?
		List<TbSpecificationOption> tbSpecificationOptionList = specificationOptionMapper.selectByExample(example);
		
		specification.setTbSpecificationOptionList(tbSpecificationOptionList);
		
		return specification;
	}

	@Override
	public void update(Specification specification) {
		TbSpecification tbSpecification = specification.getTbSpecification();
		specificationMapper.updateByPrimaryKey(tbSpecification);
		

		//		先删除此规格下的所有规格项
//		delete from tb_specification_option where spec_id=?
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		example.createCriteria().andSpecIdEqualTo(tbSpecification.getId());
		specificationOptionMapper.deleteByExample(example);
		
//		然后再重新添加页面上传过来的数据
		List<TbSpecificationOption> tbSpecificationOptionList = specification.getTbSpecificationOptionList();
		for (TbSpecificationOption tbSpecificationOption : tbSpecificationOptionList) {
			tbSpecificationOption.setSpecId(tbSpecification.getId());
			specificationOptionMapper.insert(tbSpecificationOption);
		}
//		
		
	}

	@Override
	public void dele(Long[] ids) {
		for (Long long1 : ids) {
			specificationMapper.deleteByPrimaryKey(long1);//删除主表
			
//			删除从表  tb_specification_option
			TbSpecificationOptionExample example = new TbSpecificationOptionExample();
			example.createCriteria().andSpecIdEqualTo(long1);
			specificationOptionMapper.deleteByExample(example );
			
		}
		
		
	}

	@Override
	public PageResult findPage(int pageNo, int pageSize, TbSpecification specification) {
//		使用pageHelper 分页
		PageHelper.startPage(pageNo, pageSize);
		TbSpecificationExample example = new TbSpecificationExample(); 
		Criteria criteria = example.createCriteria();
		//		使用Page强转结果
		Page<TbSpecification> page = (Page<TbSpecification>)specificationMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<Map> findSpecList() {
		return specificationMapper.findSpecList();
	}

}
