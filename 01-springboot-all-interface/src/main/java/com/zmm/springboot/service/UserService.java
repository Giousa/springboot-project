package com.zmm.springboot.service;

import com.zmm.springboot.model.User;

import java.util.List;
import java.util.Map;

/**
 * Description:
 * Author:zhangmengmeng
 * Date:2019/3/27
 * Email:65489469@qq.com
 */
public interface UserService {

    List<User> getUserByPage(Map<String,Object> paramMap);

    User getUserById(Integer id);

    int getUserTotals();

    void addUser(User user);

    void updateUser(User user);

    void delete(Integer id);
}

