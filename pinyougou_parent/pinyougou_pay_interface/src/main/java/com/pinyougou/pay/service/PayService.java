package com.pinyougou.pay.service;

import java.util.Map;

public interface PayService {

	Map createNative(String userId) throws Exception;

	Map queryOrder(String out_trade_no) throws Exception ;

	void updatePayLog(String transaction_id,String userId);

}
