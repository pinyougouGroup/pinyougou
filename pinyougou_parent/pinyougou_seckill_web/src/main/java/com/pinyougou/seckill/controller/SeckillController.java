package com.pinyougou.seckill.controller;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.seckill.service.SeckillService;

import entity.Result;

@RestController
@RequestMapping("/seckill")
public class SeckillController {

	@Reference
	private SeckillService seckillService;
	
	@RequestMapping("/findAll")
	public List<TbSeckillGoods>  findAll(){
		return seckillService.findAllFromRedis();
	}
	
	@RequestMapping("/findOne")
	public  TbSeckillGoods  findOne(Long id){
		return seckillService.findFromRedis(id);
	}
	
	@RequestMapping("/saveSeckillOrder")
	public  Result  saveSeckillOrder(Long id){
//		判断是否有登陆人
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		if("anonymousUser".equals(userId)) {
			return new Result(false, "请登录");
		}
		try {
			seckillService.saveSeckillOrder(id,userId);
			return new Result(true, "");
		} catch (RuntimeException e) {
			return new Result(false,e.getMessage());
		}
		catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "保存订单异常");
		}
	}
}
