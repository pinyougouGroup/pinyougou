package com.pinyougou.portal.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.pojo.TbContent;

@RestController
@RequestMapping("/index")
public class IndexController {

	@Reference
	private ContentService contentService;
	
	@RequestMapping("/findByCategoryId/{categoryId}")
	public List<TbContent> findByCategoryId(@PathVariable("categoryId") Long categoryId){
		return contentService.findByCategoryId(categoryId);
	}
	@RequestMapping("/showUserName")
	public String showUserName(HttpServletRequest request) {
		HttpSession session = request.getSession();
		ThirdPartyUser thirdPartyUser = (ThirdPartyUser) session.getAttribute("param");
		String userName = thirdPartyUser.getUserName();
		String imgurl = thirdPartyUser.getAvatarUrl();
		return userName;
	}
}
