package com.pinyougou.itempage.service.impl;

import java.io.File;
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

public class ItempageDeleteConsumer implements MessageListener {

	
	@Autowired
	private ItempageService itempageService;
	@Override
	public void onMessage(Message arg0) {
		TextMessage message = (TextMessage) arg0;
		try {
			String goodsId = message.getText();
			Goods goods = itempageService.findGoodsById(Long.parseLong(goodsId));
			for (TbItem tbItem : goods.getItemList()) {
				new File("D:\\class52\\html\\"+tbItem.getId()+".html").delete();
			}
			System.out.println("静态页同步删除完成");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
