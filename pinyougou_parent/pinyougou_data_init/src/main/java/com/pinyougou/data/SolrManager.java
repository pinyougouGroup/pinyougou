package com.pinyougou.data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath*:spring/applicationContext*.xml")
public class SolrManager {
	
	@Autowired
	private SolrTemplate solrTemplate;
	
	@Autowired
	private TbItemMapper itemMapper;
	
	@Test
	public void testDelAll() {
//		solrTemplate.deleteById("1"); //按照id删除
		SolrDataQuery query = new SimpleQuery("*:*"); //先搜索后删除    
		solrTemplate.delete(query);
		solrTemplate.commit();
	}
	
	@Test
	public void testAddAll() {
		
//		查询所有已上架的商品 sku数据
		List<TbItem> list = itemMapper.findAllGrounding();
		for (TbItem tbItem : list) {
			Map<String,String>   specMap = JSON.parseObject(tbItem.getSpec(), Map.class);  
			tbItem.setSpecMap(specMap);
		}
		
		solrTemplate.saveBeans(list);
		solrTemplate.commit();
		System.out.println("添加成功");
		
	}
	
	
	@Test
	public void testAdd() {
		TbItem item = new TbItem();
		item.setId(1211l);
		item.setGoodsId(12121212l);
		item.setTitle("小米6X 32G 双卡");
		item.setPrice(new BigDecimal("1399"));
		item.setBrand("小米");
		
//		item_spec_机身内存:32G
//		item_spec_网络制式:双卡
		
		
//		item_spec_容量:1000ml
		
		solrTemplate.saveBean(item);
		solrTemplate.commit();
		System.out.println("添加成功");
		
	}
	

}
