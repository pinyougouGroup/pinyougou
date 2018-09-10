package com.pinyougou.itempage.service.impl;

import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.itempage.service.ItempageService;
import com.pinyougou.pojo.TbItem;

import freemarker.template.Configuration;
import freemarker.template.Template;
import groupEntity.Goods;

public class ItempageConsumer implements MessageListener {

	@Autowired
	private FreeMarkerConfigurer freemarkerConfig;
	
	@Autowired
	private ItempageService itempageService;
	@Override
	public void onMessage(Message arg0) {
		TextMessage message = (TextMessage) arg0;
		try {
			String goodsId = message.getText();
			
			Configuration configuration = freemarkerConfig.getConfiguration();
			Template template = configuration.getTemplate("item.ftl");
			Map map = null;
//		准备数据集  goods组合类
			Goods goods = itempageService.findGoodsById(Long.parseLong(goodsId));
//			根据一个spu生成此spu下所有sku的页面
			
			for (TbItem tbItem : goods.getItemList()) {
				map = new HashMap();
				
				map.put("goods", goods);
				map.put("tbItem", tbItem);//当前的sku
				
				Writer wirter = new FileWriter("D:\\class52\\html\\"+tbItem.getId()+".html");
				template.process(map, wirter);
				wirter.close();
			}
			System.out.println("静态页同步完成");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
