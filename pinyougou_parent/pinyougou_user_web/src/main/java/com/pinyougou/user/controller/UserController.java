package com.pinyougou.user.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;

import entity.Result;

@RestController
@RequestMapping("/user")
public class UserController {

	@Reference
	private UserService userService;
	
	@RequestMapping("/showName")
	public String showName() {
		 return SecurityContextHolder.getContext().getAuthentication().getName();
	}
	
	@RequestMapping("/sendSmsCode/{phone}")
	public Result sendSmsCode(@PathVariable("phone") String phone) {
		try {
			userService.sendSmsCode(phone);
			return new Result(true, "发送成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "发送失败");
		}
	}
	
//	/user/add/"+code,user
	@RequestMapping("/add/{code}")
	public Result sendSmsCode(@PathVariable("code") String code,@RequestBody TbUser user) {
		try {
			userService.add(user,code);
			return new Result(true, "注册成功");
		} catch (RuntimeException e) {
			e.printStackTrace();
			return new Result(false, e.getMessage());
		}catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "注册失败");
		}
		
		
	}
}
