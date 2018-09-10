package com.pinyougou.order.service;

import java.util.List;

import com.pinyougou.pojo.TbAddress;

public interface AddressService {

	List<TbAddress> findAddressListByUserId(String userId);

}
