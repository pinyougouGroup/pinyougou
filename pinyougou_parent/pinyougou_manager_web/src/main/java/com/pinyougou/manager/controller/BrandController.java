package com.pinyougou.manager.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;

import entity.PageResult;
import entity.Result;

//ip:port/brand/findAll

@RequestMapping("/brand")
@RestController
public class BrandController {
	
	@Reference
	private BrandService brandService;
	
//	添加模板数据时需要的品牌数据 要求返回的格式：[{id:1,text:"SSS"},{}...]
	@RequestMapping("/findBrandList")
	public List<Map> findBrandList() {
		return brandService.findBrandList();
	}
	
	 
	
	@RequestMapping("findAll")
	public List<TbBrand> findAll() {
		return brandService.findAll();
	}
	
	
//	findPage/"+pageNo+"/"+pageSize
	@RequestMapping("findPage/{pageNo}/{pageSize}")
	public PageResult findPage(@PathVariable("pageNo") int pageNo,@PathVariable("pageSize") int pageSize) {
//		{total:100,rows:[{},{},{}]}
		return brandService.findPage(pageNo,pageSize);
	}
	
	
	
	@RequestMapping("search/{pageNo}/{pageSize}")
	public PageResult search(@RequestBody TbBrand brand, @PathVariable("pageNo") int pageNo,@PathVariable("pageSize") int pageSize) {
//		{total:100,rows:[{},{},{}]}
		return brandService.findPage(pageNo,pageSize,brand);
	}
	
	
	@RequestMapping("/add")
	public Result  add(@RequestBody TbBrand brand) {
//		{success:true|false,message:"保存成功"|"保存失败"}
		try {
			brandService.add(brand);
			return new Result(true, "保存成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "保存失败");
		}
	}
	
	@RequestMapping("/update")
	public Result  update(@RequestBody TbBrand brand) {
//		{success:true|false,message:"保存成功"|"保存失败"}
		try {
			brandService.update(brand);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}
	
	@RequestMapping("/dele/{ids}")
	public Result  dele(@PathVariable("ids") Long[] ids) {
//		{success:true|false,message:"保存成功"|"保存失败"}
		try {
			brandService.dele(ids);
			return new Result(true, "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
	@RequestMapping("/findOne/{id}")
	public TbBrand findOne(@PathVariable("id") Long id) {
		return brandService.findOne(id);
	}
	

}
