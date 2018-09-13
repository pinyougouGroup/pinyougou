package com.pinyougou.sellergoods.service;

import entity.PageResult;

public interface SeckillOrderService {

	PageResult findPage(int pageNo, int pageSize);

}
