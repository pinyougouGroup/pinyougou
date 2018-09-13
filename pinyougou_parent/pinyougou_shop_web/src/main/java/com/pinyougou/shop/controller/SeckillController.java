package com.pinyougou.shop.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.sellergoods.service.SeckillService;

import entity.Result;

@RestController
@RequestMapping("/seckill")
public class SeckillController {

	@Reference
	private SeckillService seckillService;
	
	@RequestMapping("/findAll")
	public List<TbSeckillGoods> findAll(){
		return seckillService.findAll();
	}
	
	@RequestMapping("/add")
	public Result add(@RequestBody TbSeckillGoods tbSeckillGoods){
		try {
			seckillService.add(tbSeckillGoods);
			return new Result(true,"添加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"添加失败");
		}
		
	}
	
	
	
}
