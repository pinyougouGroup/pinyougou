package com.pinyougou.user.service;

import com.pinyougou.pojo.TbUser;

public interface UserService {

	void sendSmsCode(String phone) throws Exception;

	void add(TbUser user, String code);

}
