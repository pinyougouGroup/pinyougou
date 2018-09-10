package com.pinyougou.manager.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.sellergoods.service.SpecificationService;

import entity.PageResult;
import entity.Result;
import groupEntity.Specification;

//ip:port/specification/findAll

@RequestMapping("/specification")
@RestController
public class SpecificationController {
	
	@Reference
	private SpecificationService specificationService;
	
	@RequestMapping("/findSpecList")
	public List<Map> findSpecList() {
		return specificationService.findSpecList();
	}
	
	@RequestMapping("findAll")
	public List<TbSpecification> findAll() {
		return specificationService.findAll();
	}
	
	
//	findPage/"+pageNo+"/"+pageSize
	@RequestMapping("findPage/{pageNo}/{pageSize}")
	public PageResult findPage(@PathVariable("pageNo") int pageNo,@PathVariable("pageSize") int pageSize) {
//		{total:100,rows:[{},{},{}]}
		return specificationService.findPage(pageNo,pageSize);
	}
	
	
	
	@RequestMapping("search/{pageNo}/{pageSize}")
	public PageResult search(@RequestBody TbSpecification specification, @PathVariable("pageNo") int pageNo,@PathVariable("pageSize") int pageSize) {
//		{total:100,rows:[{},{},{}]}
		return specificationService.findPage(pageNo,pageSize,specification);
	}
	
	
	@RequestMapping("/add")
	public Result  add(@RequestBody Specification specification) {
//		{success:true|false,message:"保存成功"|"保存失败"}
		try {
			specificationService.add(specification);
			return new Result(true, "保存成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "保存失败");
		}
	}
	
	@RequestMapping("/update")
	public Result  update(@RequestBody Specification specification) {
//		{success:true|false,message:"保存成功"|"保存失败"}
		try {
			specificationService.update(specification);
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
			specificationService.dele(ids);
			return new Result(true, "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
	@RequestMapping("/findOne/{id}")
	public Specification findOne(@PathVariable("id") Long id) {
		return specificationService.findOne(id);
	}
	

}
