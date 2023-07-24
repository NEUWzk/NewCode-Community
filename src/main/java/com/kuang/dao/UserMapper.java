package com.kuang.dao;

import com.kuang.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserMapper {

    User selectById(int id);
    //根据用户名查询
    User selectByName(String username);
    //根据邮箱查询
    User selectByEmail(String email);
    //插入用户，返回受影响行数
    int insertUser(User user);
    //更新状态数据，返回修改条数
    int updateStatus(int id, int status);
    //更新头像路径
    int updateHeader(int id, String headerUrl);
    //更改密码
    int updatePassword(int id,String password);

    int updateStatusByUserid(int id,int status);

}
