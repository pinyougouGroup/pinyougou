package com.pinyougou.itempage.service;

import java.util.List;

import groupEntity.Goods;

public interface ItempageService {

	Goods findGoodsById(Long goodsId);

	List<Goods> findAllGoods();

}
