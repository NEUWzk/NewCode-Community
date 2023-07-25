package com.kuang.dao;

import com.kuang.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LoginTicketMaper {

    @Insert("insert into login_ticket(user_id,ticket,status,expired) " +
            "values(#{userId},#{ticket},#{status},#{expired})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);


    @Select("select * from login_ticket where ticket = #{ticket}")
    LoginTicket selectByTicket(String ticket);


    @Update("update login_ticket set status = #{status} " +
            "where ticket = #{ticket}")
    int updateStatus(String ticket, int status);
}
