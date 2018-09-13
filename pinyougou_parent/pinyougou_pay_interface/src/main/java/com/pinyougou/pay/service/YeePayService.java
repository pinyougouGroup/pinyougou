package com.pinyougou.pay.service;

import java.util.Map;

public interface YeePayService {

	Map findOrderByUserId(String userId);

	String confirmOrder(String pd_FrpId,String userId);

	String callback(String p1_MerId, String r0_Cmd, String r1_Code, String r2_TrxId, String r3_Amt, String r4_Cur,
			String r5_Pid, String r6_Order, String r7_Uid, String r8_MP, String r9_BType, String rb_BankId,
			String ro_BankOrderId, String rp_PayDate, String rq_CardNo, String ru_Trxtime, String hmac);

}
