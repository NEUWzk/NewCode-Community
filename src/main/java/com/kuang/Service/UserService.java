package com.kuang.Service;

import com.kuang.dao.UserMapper;
import com.kuang.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User fingUserById(int id)
    {
        return userMapper.selectById(id);
    }
}


