package com.pinyougou.cart.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrder;

import entity.Result;

@RestController
@RequestMapping("/order")
public class OrderController {

	@Reference
	private OrderService orderService;
	@RequestMapping("/save")
	public Result save(@RequestBody TbOrder order) {
		
		try {
			order.setUserId(SecurityContextHolder.getContext().getAuthentication().getName());
			orderService.save(order);
			return new Result(true, "");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "保存订单失败");
		}
	}
}
