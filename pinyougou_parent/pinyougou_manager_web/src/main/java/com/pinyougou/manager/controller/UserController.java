package com.pinyougou.manager.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

	
	@RequestMapping("/showName")
	public String showName() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
		
	}
}

