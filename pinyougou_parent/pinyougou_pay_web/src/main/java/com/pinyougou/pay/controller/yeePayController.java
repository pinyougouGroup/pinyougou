package com.pinyougou.pay.controller;

import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.PayService;
import com.pinyougou.pay.service.YeePayService;

import entity.Result;

@Controller
@RequestMapping("/yeePay")
public class yeePayController {

	@Reference
	private YeePayService  yeePayService;
	
	@RequestMapping("/findOrderByUserId")
	@ResponseBody
	public Map findOrderByUserId() {
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		return yeePayService.findOrderByUserId(userId);
		
	}
	
	@RequestMapping("/confirmOrder/{pd_FrpId}")
	@ResponseBody
	public String confirmOrder(@PathVariable("pd_FrpId") String pd_FrpId) {
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		return yeePayService.confirmOrder(pd_FrpId,userId);
	}
	
	@RequestMapping("/callback")
	public String callback(String p1_MerId,String r0_Cmd,String r1_Code,String r2_TrxId,String r3_Amt,String r4_Cur,String r5_Pid,String r6_Order,String r7_Uid
			,String r8_MP,String r9_BType,String rb_BankId,String ro_BankOrderId,String rp_PayDate,String rq_CardNo,String ru_Trxtime,String hmac) {
		
		String path = yeePayService.callback(p1_MerId,r0_Cmd,r1_Code,r2_TrxId,r3_Amt,r4_Cur,r5_Pid,r6_Order,r7_Uid,r8_MP,r9_BType,rb_BankId,ro_BankOrderId,
				rp_PayDate,rq_CardNo,ru_Trxtime,hmac);
		
		return "redirect:"+path;
	}
	
}
