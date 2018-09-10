package com.pinyougou.search.service.impl;

import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;

public class SolrConsumer implements MessageListener {

	@Autowired
	private TbItemMapper itemMapper;
	
	@Autowired
	private SolrTemplate solrTemplate;
	
	@Override
	public void onMessage(Message arg0) {
		 TextMessage message = (TextMessage) arg0;
		 try {
			String goodsId = message.getText();

			//			根据goodsId查询所有的sku，保存到solr索引库
			TbItemExample example= new TbItemExample();
			example.createCriteria().andGoodsIdEqualTo(Long.parseLong(goodsId));
			List<TbItem> itemList = itemMapper.selectByExample(example);
			for (TbItem tbItem : itemList) {
				Map<String, String> specMap = JSON.parseObject(tbItem.getSpec(), Map.class);
				tbItem.setSpecMap(specMap);
			}
			solrTemplate.saveBeans(itemList); //相当于saveOrUpdate()
			solrTemplate.commit();
			System.out.println("solr同步成功");
		} catch (JMSException e) {
			
			e.printStackTrace();
		}
	}

}
