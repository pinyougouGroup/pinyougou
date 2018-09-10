package com.pinyougou.pay.controller;

import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.PayService;

import entity.Result;

@RestController
@RequestMapping("/pay")
public class PayController {

	@Reference
	private PayService  payService;
	
	@RequestMapping("/createNative")
	public Map createNative() {
		try {
			String userId = SecurityContextHolder.getContext().getAuthentication().getName();
			return payService.createNative(userId);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	@RequestMapping("/queryOrder/{out_trade_no}")
	public Result queryOrder(@PathVariable("out_trade_no") String out_trade_no) {
	      try {
//	    	  3分钟  180秒  60次
	    	  int times = 0;
	    	  while (times<10) {
			        Thread.sleep(3000);
					Map resulMap = payService.queryOrder(out_trade_no);
					times++;
		//			trade_state=NOTPAY
					if(resulMap.get("trade_state").equals("SUCCESS")) {
//							修改PayLog
//							修改订单
						String transaction_id = (String) resulMap.get("transaction_id");
						payService.updatePayLog(transaction_id,SecurityContextHolder.getContext().getAuthentication().getName());
						return new Result(true, "支付成功");
					}
						System.out.println("执行了"+times+"次");
	    	  }
	    	  return new Result(false, "支付超时");
			
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "支付异常");
		}
		
		 
	}
}
