package com.pinyougou.manager.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.sellergoods.service.SeckillService;

import entity.PageResult;
import entity.Result;

@RestController
@RequestMapping("/seckill")
public class SeckillController {
	@Reference
	private SeckillService seckillService;

	@RequestMapping("/search/{pageNum}/{pageSize}")
	public PageResult search(@RequestBody TbSeckillGoods tbSeckillGoods, @PathVariable("pageNum") int pageNum,
			@PathVariable("pageSize") int pageSize) {
		return seckillService.search(tbSeckillGoods, pageNum, pageSize);

	}
	@RequestMapping("/findOne/{id}")
	public TbSeckillGoods findOne(@PathVariable("id") Long id){
		return seckillService.findOne(id);		
	}
	@RequestMapping("/updateStatus/{status}/{id}")
	public Result updateStatus(@PathVariable("status") String status,@PathVariable("id") Long id){
		try {
			
			seckillService.updateStatus(status,id);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}
	
}
