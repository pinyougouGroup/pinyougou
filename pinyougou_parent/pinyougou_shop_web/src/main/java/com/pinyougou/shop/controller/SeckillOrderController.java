package com.pinyougou.shop.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.sellergoods.service.SeckillOrderService;

import entity.PageResult;

@RestController
@RequestMapping("/seckillOrder")
public class SeckillOrderController {
	
	@Reference
	private SeckillOrderService seckillOrderService;
	

	
	@RequestMapping("/findPage/{pageNo}/{pageSize}")
	public PageResult findPage(@PathVariable("pageNo") int pageNo, @PathVariable("pageSize") int pageSize) {
		return seckillOrderService.findPage(pageNo, pageSize);
	}
}
