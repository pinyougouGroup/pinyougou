package com.pinyougou.itempage.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.itempage.service.ItempageService;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;

import groupEntity.Goods;

@Service
public class ItempageServiceImpl implements ItempageService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	@Autowired
	private TbItemMapper itemMapper;
	
	@Autowired
	private TbItemCatMapper itemCatMapper;
	
	
	@Override
	public Goods findGoodsById(Long goodsId) {
		Goods goods = new Goods();
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(goodsId);
		goods.setTbGoods(tbGoods);
		TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
		goods.setTbGoodsDesc(tbGoodsDesc);
		
		TbItemExample example = new TbItemExample();
		example.createCriteria().andGoodsIdEqualTo(goodsId);
		List<TbItem> itemList = itemMapper.selectByExample(example );
		goods.setItemList(itemList);
		
		Map categoryMap = new HashMap<>();
		categoryMap.put("categoty1", itemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id()).getName());
		categoryMap.put("categoty2", itemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id()).getName());
		categoryMap.put("categoty3", itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id()).getName());
		
		goods.setCategoryMap(categoryMap );
		return goods;
	}


	@Override
	public List<Goods> findAllGoods() {
		List<Goods> goodsList = new ArrayList<Goods>();
		List<TbGoods> selectByExample = goodsMapper.selectByExample(null);
		for (TbGoods tbGoods : selectByExample) {
			Goods goods = findGoodsById(tbGoods.getId());
			goodsList.add(goods);
		}
		return goodsList;
	}

}
