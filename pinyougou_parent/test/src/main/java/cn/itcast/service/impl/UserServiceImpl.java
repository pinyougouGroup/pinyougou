package cn.itcast.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.itcast.mapper.UserMapper;
import cn.itcast.pojo.User;
import cn.itcast.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserMapper userDao;
	
	public List<User> findAll() {
		return userDao.selectByExample(null);
	}

	@Override
	public User findById(Integer id) {
		return userDao.selectByPrimaryKey(id);
	}

	@Override
	public void update(User user) {
		userDao.updateByPrimaryKey(user);
	}

}
