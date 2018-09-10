package com.pinyougou.itempage.controller;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.itempage.service.ItempageService;
import com.pinyougou.pojo.TbItem;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;
import groupEntity.Goods;

@RestController
@RequestMapping("/itempage")
public class ItempageController {
	
	@Reference
	private ItempageService  itempageService;
	
	@Autowired
	private FreeMarkerConfigurer freeMarkerConfigurer;
	
	@RequestMapping("/generatorToHtmlByGoodsId")
	public String generatorToHtmlByGoodsId(Long goodsId) {
		try {
			Configuration configuration = freeMarkerConfigurer.getConfiguration();
			Template template = configuration.getTemplate("item.ftl");
			Map map = null;
//		准备数据集  goods组合类
			Goods goods = itempageService.findGoodsById(goodsId);
//			根据一个spu生成此spu下所有sku的页面
			
			for (TbItem tbItem : goods.getItemList()) {
				map = new HashMap();
				
				map.put("goods", goods);
				map.put("tbItem", tbItem);//当前的sku
				
				Writer wirter = new FileWriter("D:\\class52\\html\\"+tbItem.getId()+".html");
				template.process(map, wirter);
				wirter.close();
			}
			
			
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		} 
	}
	@RequestMapping("/generatorToHtmlAll")
	public String generatorToHtmlAll() {
		try {
			Configuration configuration = freeMarkerConfigurer.getConfiguration();
			Template template = configuration.getTemplate("item.ftl");
			Map map = null;
//		准备数据集  goods组合类
//			Goods goods = itempageService.findGoodsById(goodsId);
			List<Goods> goodsList = itempageService.findAllGoods();
			for (Goods goods : goodsList) {
//				根据一个spu生成此spu下所有sku的页面
				for (TbItem tbItem : goods.getItemList()) {
					map = new HashMap();
					map.put("goods", goods);
					map.put("tbItem", tbItem);//当前的sku
					Writer wirter = new FileWriter("D:\\class52\\html\\"+tbItem.getId()+".html");
					template.process(map, wirter);
					wirter.close();
				}
			}
//			
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		} 
	}
}
