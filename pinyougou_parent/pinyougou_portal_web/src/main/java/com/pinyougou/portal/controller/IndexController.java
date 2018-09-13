package com.pinyougou.portal.controller;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.pojo.TbContent;
@CrossOrigin(origins= "*")
@RestController
@RequestMapping("/index")
public class IndexController {

	@Reference
	private ContentService contentService;
	
	@RequestMapping("/findByCategoryId/{categoryId}")
	public List<TbContent> findByCategoryId(@PathVariable("categoryId") Long categoryId){
		return contentService.findByCategoryId(categoryId);
	}
	
	@RequestMapping("/findUserName")
	public void findUserName() throws IOException, Exception {
		/*HttpClient httpClient=new HttpClient("http://192.168.181.70:9980/sinalogin/showUserName");
		httpClient.post();
		String string = httpClient.getContent();
		System.out.println(string);*/
		
		CloseableHttpClient httpClient = HttpClients.createDefault(); //打开浏览器
//		HttpGet httpGet = new HttpGet("http://www.baidu.com/");
//		HttpGet httpGet = new HttpGet("http://www.baidu.com/s?wd=短信");
		//HttpGet httpGet = new HttpGet("http://192.168.181.70:9980/sinalogin/showUserName");
		// 创建Http Post请求  
        HttpPost httpPost = new HttpPost("http://192.168.181.70:9980/sinalogin/showUserName");  
        // 创建请求内容  
        StringEntity entity = new StringEntity("{'withCredentials':true}", ContentType.APPLICATION_JSON);  
        httpPost.setEntity(entity);  
		CloseableHttpResponse response = httpClient.execute(httpPost);
//		200  302 404 500
		//if(response.getStatusLine().getStatusCode() ==200) {
			String string = EntityUtils.toString(response.getEntity(), "utf-8");
			System.out.println(string);
		//}
		
		
	}
}
