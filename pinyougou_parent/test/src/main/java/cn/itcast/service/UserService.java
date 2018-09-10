package cn.itcast.service;

import java.util.List;

import cn.itcast.pojo.User;


public interface UserService {

	List<User> findAll();

	User findById(Integer id);

	void update(User user);

}
