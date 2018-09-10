package com.pinyougou.search.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleFilterQuery;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.data.solr.core.query.result.GroupPage;
import org.springframework.data.solr.core.query.result.GroupResult;
import org.springframework.data.solr.core.query.result.HighlightEntry.Highlight;
import org.springframework.data.solr.core.query.result.HighlightPage;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.SearchService;

@Service
public class SearchServiceImpl implements SearchService {

	@Autowired
	private SolrTemplate solrTemplate;
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	@Override
	public Map findByParamMap(Map paramMap) {
		Map resultMap = new HashMap();
		
		
		List<String> categoryList = new ArrayList<String>();
	
		//		根据关键字分组查询分类     select categrory from tb_item where item_keywords like ? group by categrory 手机 平板电脑
		Query groupQuery = new SimpleQuery(new Criteria("item_keywords").is(paramMap.get("keyword")));
		GroupOptions groupOptions = new GroupOptions();
		groupOptions.addGroupByField("item_category");
		groupQuery.setGroupOptions(groupOptions);
		GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(groupQuery, TbItem.class);
		GroupResult<TbItem> groupResult = groupPage.getGroupResult("item_category");
		Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
		for (GroupEntry<TbItem> groupEntry : groupEntries) {
			categoryList.add(groupEntry.getGroupValue());
		}
		
		resultMap.put("categoryList", categoryList);
		
		if(categoryList.size()>0) {
			categoryList.get(0);
			//		根据第一个商品分类 查询品牌列表（从redis中获取）
			List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("search_categroy_brand").get(categoryList.get(0));
			resultMap.put("brandList", brandList);
			//			根据第一个商品分类 查询规格列表（从redis中获取）
			List<Map> specList = (List<Map>) redisTemplate.boundHashOps("search_categroy_spec").get(categoryList.get(0));
			resultMap.put("specList", specList);
		}
		
		
//		根据关键字搜索--springdataSolr
		HighlightQuery query = new SimpleHighlightQuery(new Criteria("item_keywords").is(paramMap.get("keyword")));
		

		//		开启高亮的设置
		HighlightOptions highlightOption = new HighlightOptions();
		highlightOption.addField("item_title");//指定高亮哪个域
		highlightOption.setSimplePrefix("<span style=\"color:red\">");
		highlightOption.setSimplePostfix("</span>");
		query.setHighlightOptions(highlightOption);
		
//		添加过滤条件
//		分类
		if(!paramMap.get("category").equals("")) {
			query.addFilterQuery(new SimpleFilterQuery(new Criteria("item_category").is(paramMap.get("category"))));
		}
//		品牌
		if(!paramMap.get("brand").equals("")) {
			query.addFilterQuery(new SimpleFilterQuery(new Criteria("item_brand").is(paramMap.get("brand"))));
		}
//		规格
		Map<String,String> specMap = (Map) paramMap.get("spec"); //{"网络":"联通3G","机身内存":"32G"}
		for(String key:specMap.keySet()) {
			query.addFilterQuery(new SimpleFilterQuery(new Criteria("item_spec_"+key).is(specMap.get(key))));
		}
		
//		价格  paramMap.get("price") 0-500  500-100  3000-*
		
		if(!paramMap.get("price").equals("")) {
			String[] split = (paramMap.get("price")+"").split("-");
			if(!split[1].equals("*")) {
				query.addFilterQuery(new SimpleFilterQuery(new Criteria("item_price").between(split[0], split[1], true, true) ));

			}else {
				query.addFilterQuery(new SimpleFilterQuery(new Criteria("item_price").greaterThanEqual(split[0]) ));

			}
		}
		
//		价格排序
		if(paramMap.get("order").equals("desc")) {
			query.addSort(new Sort(Direction.DESC, "item_price"));
		}else {
			query.addSort(new Sort(Direction.ASC, "item_price"));
		}
		
//		分页   paramMap.get("pageNo"); ( pageNo-1*pageSize)
		 Integer pageNo = (Integer) paramMap.get("pageNo");
//		每页显示60条 第一页的起始位置：0
//		每页显示60条 第二页的起始位置：60
//		每页显示60条 第三页的起始位置：120
		query.setOffset((pageNo-1)*60);//起始位置
		query.setRows(60);//每页显示的条数
		
		
		HighlightPage<TbItem> highlightPage = solrTemplate.queryForHighlightPage(query , TbItem.class);
	
		resultMap.put("total", 	highlightPage.getTotalElements());//总条数
		resultMap.put("totalPages", 	highlightPage.getTotalPages()); //总页数
		
		System.out.println(JSON.toJSONString(highlightPage, true));
		List<TbItem> itemList = highlightPage.getContent();//每页显示的数据
		for (TbItem tbItem : itemList) {
			List<Highlight> highlights = highlightPage.getHighlights(tbItem);
			if(highlights!=null&&highlights.size()>0) {
				 List<String> snipplets = highlights.get(0).getSnipplets();
				 if(snipplets!=null&&snipplets.size()>0) {
					 String highLightTitle = snipplets.get(0);
					 tbItem.setTitle(highLightTitle);
				 }
			}
		}
		 
		resultMap.put("itemList", itemList);
		return resultMap;
	}

}
