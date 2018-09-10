package com.pinyougou.search.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.search.service.SearchService;

@RestController
@RequestMapping("/search")
public class SearchController {
	
	@Reference
	private SearchService searchService;
	
	@RequestMapping("/findByParamMap")
	public Map findByParamMap(@RequestBody Map paramMap) {
		
		return searchService.findByParamMap(paramMap);
		
	}

}
