package com.pinyougou.cart.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;

import entity.Result;
import groupEntity.Cart;
import groupEntity.CartVo;
import util.CookieUtil;

@RestController
@RequestMapping("/cart")
public class CartController {

	
	@Reference
	private CartService cartService;
	@Autowired
	private HttpSession session;
	
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private HttpServletResponse response;
	
	private String getSessionId() {
		String sessionId = CookieUtil.getCookieValue(request, "user_key", "utf-8");
		if(sessionId==null) {
			sessionId = session.getId();
			CookieUtil.setCookie(request, response, "user_key", sessionId, 48*60*60, "utf-8");
		}
		return sessionId;
		
	}

	/*@RequestMapping("/findCartList")
	public List<Cart> findCartList(){
		String sessionId = getSessionId();
		
		
		
		List cartList_session =	cartService.findCartListFromRedis(sessionId);   
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		if(!"anonymousUser".equals(userId)) {
//			意味着有当前登录人
			List cartList_userId = cartService.findCartListFromRedis(userId); 
			if(cartList_session.size()!=0) {
	//			购物车合并
				cartList_userId  = cartService.mergeCartList(cartList_session,cartList_userId);
				cartService.saveCartListToRedis(userId, cartList_userId); //把合并后的购物车列表再放到Redis中
	//			清空Redis数据
				cartService.clearRedisByKey(sessionId); 
			}
			return cartList_userId;
		}
		
		

		return cartList_session;
	}
	
	*/
	//@RequestMapping("/addGoodsToCartList/{itemId}/{num}")
//	跨域：只要域名不一样就是跨域   https:// localhost:8086   http:// localhost:8086 
	/*@CrossOrigin(origins= {"http://item.pinyougou.com","http://www.pinyougou.com"})  //允许item.pinyougou.com网站异步请求此方法
	public Result addGoodsToCartList(@PathVariable("itemId") Long itemId,@PathVariable("num") int num){
		try {
			List<Cart> oldCartList = findCartList();
//			向原来的购物车列表中添加商品
			List<Cart> cartList = cartService.addGoodsToCartList(oldCartList,itemId,num);
//		把构建的新的购物车列表再保存起来
			String userId = SecurityContextHolder.getContext().getAuthentication().getName();
			if(!"anonymousUser".equals(userId)) { //如果有登录人应该把userID作为key
				cartService.saveCartListToRedis(userId,cartList);
			}else {
				String sessionId = getSessionId();
				cartService.saveCartListToRedis(sessionId,cartList);
			}
			
			return new Result(true, "添加成功");
		} catch (RuntimeException e) {
			 
			e.printStackTrace();
			return new Result(false, e.getMessage());
		}catch (Exception e) {
			 
			e.printStackTrace();
			return new Result(false, "添加失败");
		}

	}*/
	@RequestMapping("addItemToCartList")
	public Result addItemToCartList(@RequestBody CartVo vo){
	 try {
		 List<Cart> cartList = vo.getCartList();//locaStorage中的数据
		 String userId=SecurityContextHolder.getContext().getAuthentication().getName();
		 if(!"anonymousUser".equals(userId)) { 
			 List<Cart> list=cartService.findCartListFromRedis(userId);
			list = cartService.addItemToCartList(list,vo.getItemId(),vo.getNum());
			 cartService.saveCartListToRedis(userId,list);
		 }else {
			 
		     cartList = cartService.addItemToCartList(vo.getCartList(),vo.getItemId(),vo.getNum());

		  }
		
	 return new Result(true, JSON.toJSONString(cartList));
	 }catch (RuntimeException e) {
			e.printStackTrace();
			return new Result(false, e.getMessage());
	 } catch (Exception e) {
	 e.printStackTrace();
	 return new Result(false, "添加失败");
	 }
	}
	
	@RequestMapping("findCartList")
	public Result findCartList(@RequestBody CartVo vo){	
	 //如果登录了就返回合并后的代码如果没登录就返回原来的代码
	 String userId=SecurityContextHolder.getContext().getAuthentication().getName();
	 List<Cart> cartList=null;
	 if(!"anonymousUser".equals(userId)) { 
		 cartList = cartService.mergeLocalStrageAndRedis(userId,vo);
	     return new Result(true,JSON.toJSONString(cartList));
	 }else {
		 cartList=vo.getCartList();	
	     return new Result(false,JSON.toJSONString(cartList));

	  }
	 
	}
	
}
