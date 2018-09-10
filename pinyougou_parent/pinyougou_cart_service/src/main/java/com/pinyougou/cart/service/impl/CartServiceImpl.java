package com.pinyougou.cart.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;

import groupEntity.Cart;
@Service
public class CartServiceImpl implements CartService {

	@Autowired
	private RedisTemplate redisTemplate;
	
	@Autowired
	private TbItemMapper itemMapper;
	
	@Override
	public List<Cart> findCartListFromRedis(String sessionId) {
		List<Cart> list = (List<Cart>) redisTemplate.boundHashOps("cartList").get(sessionId);
		if(list==null) {
			list = new ArrayList<Cart>(0);
		}
		return list;
	}

	@Override
	public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, int num) {
		
		TbItem  tbItem = itemMapper.selectByPrimaryKey(itemId);
		if(tbItem==null) {
			throw new RuntimeException("没有此商品");
		}
	 
		String sellerId = tbItem.getSellerId();
//		添加购物车时：
//		1、即将添加的商品对应的商家是否已存在购物车列表中
		Cart cart = findCartBySellerIdFromCartList(cartList, sellerId);
//		 1、1 如果已存在：判断此商品是否已在此商家的购物车对象的orderItemList中
		if(cart!=null) {
			List<TbOrderItem> orderItemList = cart.getOrderItemList();
			TbOrderItem orderItem = findOrderItemByItemIdFromOrderItemList(orderItemList,itemId);
//			  1、1、1 如果有直接更新数量
			if(orderItem!=null) {
//				更新数量
				orderItem.setNum(orderItem.getNum()+num);
//				更新合计金额
				orderItem.setTotalFee(new BigDecimal(orderItem.getNum()*orderItem.getPrice().doubleValue()));
				if(orderItem.getNum()<=0) {  //判断数量更新后是否为0
					orderItemList.remove(orderItem);
					if(orderItemList.size()==0) {
						cartList.remove(cart);
					}
				}
				
				
			}else {
//				  1、1、2 如果没有构建一个orderItem对象 把此对象放到orderItemList中
				orderItem = createTbOrderItem(orderItem, tbItem, num);
				orderItemList.add(orderItem);
			}
		}else {
//			 1、2 如果不存在 创建一个cart对象，创建一个orderItemList集合,创建orderItem对象并且放入orderItemList集合，
			cart = new Cart();
			cart.setSellerId(sellerId);
			cart.setSellerName(tbItem.getSeller());
			List<TbOrderItem> orderItemList = new ArrayList<TbOrderItem>();
			TbOrderItem orderItem = null;
			orderItem = createTbOrderItem(orderItem, tbItem, num);
			orderItemList.add(orderItem);
			cart.setOrderItemList(orderItemList);
//			需要把cart对象放到购物车列表中
			cartList.add(cart);
		}
		return cartList;
	}
	
	private TbOrderItem createTbOrderItem(TbOrderItem orderItem,TbItem tbItem,int num) {
		if(num<1) {
			throw new RuntimeException("数量非法");
		}
		
		orderItem = new TbOrderItem();
		orderItem.setGoodsId(tbItem.getGoodsId());
		orderItem.setItemId(tbItem.getId());
		orderItem.setNum(num);
		orderItem.setPicPath(tbItem.getImage());
		orderItem.setPrice(tbItem.getPrice());
		orderItem.setSellerId(tbItem.getSellerId());
		orderItem.setTitle(tbItem.getTitle());
		orderItem.setTotalFee(new BigDecimal(orderItem.getNum()*orderItem.getPrice().doubleValue()));
//		orderItemList.add(orderItem);
		return orderItem;
	}

	private TbOrderItem findOrderItemByItemIdFromOrderItemList(List<TbOrderItem> orderItemList, Long itemId) {
		for (TbOrderItem tbOrderItem : orderItemList) {
			if(tbOrderItem.getItemId().longValue() == itemId.longValue()) {
				return tbOrderItem;
			}
		}
		return null;
	}

	public Cart findCartBySellerIdFromCartList(List<Cart> cartList,String sellerId) {
		for (Cart cart : cartList) {
			if(cart.getSellerId().equals(sellerId)) {
				return cart;  //找到了此商品对象的商家的购物车对象
			}
		}
		return null;
	}
	
	
	@Override
	public void saveCartListToRedis(String sessionId, List cartList) {
		 redisTemplate.boundHashOps("cartList").put(sessionId, cartList);
	}

	@Override
	public List<Cart> mergeCartList(List<Cart> cartList_session, List<Cart> cartList_userId) {
		for (Cart cart : cartList_session) {
			List<TbOrderItem> orderItemList = cart.getOrderItemList();
			for (TbOrderItem tbOrderItem : orderItemList) {
				cartList_userId = addGoodsToCartList(cartList_userId, tbOrderItem.getItemId(), tbOrderItem.getNum());
			}
		}
		return cartList_userId;
	}

	@Override
	public void clearRedisByKey(String key) {
		 redisTemplate.boundHashOps("cartList").delete(key);
	}

}
