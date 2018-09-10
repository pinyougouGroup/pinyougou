package com.pinyougou.data;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.mapper.TbTypeTemplateMapper;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.pojo.TbTypeTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath*:spring/applicationContext*.xml")
public class RedisManager {
	
	@Autowired
	private TbItemCatMapper  itemCatMapper;
	
	@Autowired
	private TbTypeTemplateMapper  typeTemplateMapper;
	@Autowired
	private TbSpecificationOptionMapper  specificationOptionMapper;
	
	@Autowired
	private RedisTemplate redisTemplate;
	@Test
	public void initRedis() {


		List<TbItemCat> itemList = itemCatMapper.selectByExample(null);
		Long typeId = null;
		TbTypeTemplate typeTemplate = null;
		for (TbItemCat tbItemCat : itemList) {
			typeId = tbItemCat.getTypeId();
			typeTemplate = typeTemplateMapper.selectByPrimaryKey(typeId);
			List<Map> brandList = JSON.parseArray(typeTemplate.getBrandIds(), Map.class);
//			根据分类名称 初始化 品牌数据
			redisTemplate.boundHashOps("search_categroy_brand").put(tbItemCat.getName(), brandList);
			
			String specIds = typeTemplate.getSpecIds();
//			根据分类名称 初始化 规格数据
			
			List<Map> specMapList = JSON.parseArray(typeTemplate.getSpecIds(), Map.class);
			for (Map map : specMapList) {
				TbSpecificationOptionExample example = new TbSpecificationOptionExample();
				example.createCriteria().andSpecIdEqualTo( Long.parseLong( map.get("id")+""));
//				example.createCriteria().andSpecIdEqualTo( (Long)(map.get("id")));
				List<TbSpecificationOption> options = specificationOptionMapper.selectByExample(example );
//				根据每个规格ID查询规格小项
				map.put("options", options);
				
			}
			
			redisTemplate.boundHashOps("search_categroy_spec").put(tbItemCat.getName(), specMapList);

		}
		System.out.println("redis数据初始化完成");

		
	}

}
