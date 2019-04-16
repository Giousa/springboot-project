package com.zmm.springboot.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.zmm.springboot.mapper.UserMapper;
import com.zmm.springboot.model.User;
import com.zmm.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Description:
 * Author:zhangmengmeng
 * Date:2019/4/3
 * Email:65489469@qq.com
 */
@Component
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @Override
    public List<User> getUserByPage(Map<String, Object> paramMap) {
        return userMapper.selectUserByPage(paramMap);
    }

    @Override
    public User getUserById(Integer id) {
        return userMapper.selectByPrimaryKey(id);
    }

    @Override
    public int getUserTotals() {
        //字符串序列化格式 一般是key值字符串 value值不需要改动
        RedisSerializer redisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(redisSerializer);

        Integer totalRows = (Integer) redisTemplate.opsForValue().get("totalRows");

        if(totalRows == null){

            synchronized (this){

                //从redis获取一下
                totalRows = (Integer) redisTemplate.opsForValue().get("totalRows");

                if(totalRows == null){
                    System.out.println("第一次,数据库查询");
                    totalRows = userMapper.selectUserTotals();
                    redisTemplate.opsForValue().set("totalRows",totalRows);

                }else {
                    System.out.println("其他，Redis缓存查询~~");
                }
            }

        }else {
            System.out.println("缓存，Redis缓存查询~");

        }

        return totalRows;
    }

    @Override
    public void addUser(User user) {

        int add = userMapper.insert(user);
        if(add > 0){
            //更新缓存总数
            //字符串序列化格式 一般是key值字符串 value值不需要改动
            RedisSerializer redisSerializer = new StringRedisSerializer();
            redisTemplate.setKeySerializer(redisSerializer);

            redisTemplate.opsForValue().set("totalRows",userMapper.selectUserTotals());

        }
    }

    @Override
    public void updateUser(User user) {
        userMapper.updateByPrimaryKey(user);
    }

    @Override
    public void delete(Integer id) {
        int i = userMapper.deleteByPrimaryKey(id);
        if(i > 0){

            //字符串序列化格式 一般是key值字符串 value值不需要改动
            RedisSerializer redisSerializer = new StringRedisSerializer();
            redisTemplate.setKeySerializer(redisSerializer);

            redisTemplate.opsForValue().set("totalRows",userMapper.selectUserTotals());

        }
    }
}
