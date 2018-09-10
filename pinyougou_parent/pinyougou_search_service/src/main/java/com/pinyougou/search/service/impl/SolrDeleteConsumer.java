package com.pinyougou.search.service.impl;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;

public class SolrDeleteConsumer implements MessageListener {
	
	@Autowired
	private SolrTemplate solrTemplate;
	
	@Override
	public void onMessage(Message arg0) {
		 TextMessage message = (TextMessage) arg0;
		 try {
			String goodsId = message.getText();
//			SolrDataQuery query = new SimpleQuery(new Criteria("item_goodsid").equals(goodsId));
			SolrDataQuery query = new SimpleQuery("item_goodsid:"+goodsId);
			solrTemplate.delete(query);
			solrTemplate.commit();
			System.out.println("solr同步删除成功");
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
