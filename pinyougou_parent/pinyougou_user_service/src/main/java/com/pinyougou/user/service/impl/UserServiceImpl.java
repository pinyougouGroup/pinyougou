package com.pinyougou.user.service.impl;


import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbUserMapper;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;

import util.HttpClient;

@Service
public class UserServiceImpl implements UserService {

	
	@Value("${signName}")
	private String signName;
	@Value("${templateCode}")
	private String templateCode;
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	@Autowired
	private TbUserMapper userMapper;
	
	@Override
	public void sendSmsCode(String phone) throws Exception {
		String randomNumeric = RandomStringUtils.randomNumeric(6);
		
//		把随机数放到redis中  5分钟之内有效
		redisTemplate.boundValueOps("register_"+phone).set(randomNumeric, 5, TimeUnit.MINUTES);
		
        HttpClient httpClient = new HttpClient("http://localhost:7788/sms/sendSms");
        
		httpClient.addParameter("phoneNumbers", phone);
		httpClient.addParameter("signName", signName);
		httpClient.addParameter("templateCode", templateCode);
		httpClient.addParameter("templateParam", "{\"code\":\""+randomNumeric+"\"}");
		httpClient.post();
		String content = httpClient.getContent();
		System.out.println(content);
		

	}

	@Override
	public void add(TbUser user, String code) {
//		1、从redis中获取验证码
//		   判断是否存在 ，如果不存在意味着验证码失效
		String randomNumeric = (String) redisTemplate.boundValueOps("register_"+user.getPhone()).get();
		if(randomNumeric==null) {
			throw new RuntimeException("验证码失效");
		}
//		2、判断两个验证码是否一致
		if(!randomNumeric.equals(code)) {
			throw new RuntimeException("验证码错误");
		}
//		3、保存user
//		密码加密
		String md5Hex = DigestUtils.md5Hex(user.getPassword()); // 0123456789abcdef    
		user.setPassword(md5Hex);
		user.setCreated(new Date());
		user.setUpdated(new Date());
		user.setSourceType("1");
		userMapper.insert(user);
		
		redisTemplate.delete("register_"+user.getPhone());
	}

}
