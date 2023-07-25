package com.kuang.utils;

import com.kuang.entity.User;
import org.springframework.stereotype.Component;

@Component
public class HostHolder {

    private ThreadLocal<User> users = new ThreadLocal<>();

    /**
     * 从ThreadLocal线程中取出User
     **/
    public User getUser() {
        return users.get();
    }

    /**
     * 以线程为key存入User
     **/
    public void setUser(User user) {
        users.set(user);
    }

    /**
     * 释放线程
     **/
    public void clear() {
        users.remove();
    }


}
