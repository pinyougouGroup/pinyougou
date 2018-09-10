package com.pinyougou.cart.service;

import java.util.List;

import groupEntity.Cart;

public interface CartService {

	List findCartListFromRedis(String sessionId);

	List addGoodsToCartList(List<Cart> oldCartList, Long itemId, int num);

	void saveCartListToRedis(String sessionId, List cartList);

	List<Cart> mergeCartList(List<Cart> cartList_session, List<Cart> cartList_userId);

	void clearRedisByKey(String sessionId);

}
