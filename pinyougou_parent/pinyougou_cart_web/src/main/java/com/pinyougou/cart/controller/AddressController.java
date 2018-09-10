package com.pinyougou.cart.controller;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.AddressService;
import com.pinyougou.pojo.TbAddress;

@RestController
@RequestMapping("/address")
public class AddressController {

	@Reference
	private AddressService addressService;
	
	@RequestMapping("/findAddressListByUserId")
	public List<TbAddress> findAddressListByUserId(){
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		return addressService.findAddressListByUserId(userId);
	}
	
}
