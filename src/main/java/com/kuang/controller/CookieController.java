package com.kuang.controller;

import com.kuang.utils.CommunityUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class CookieController {

    @RequestMapping("/cookie/set")
    @ResponseBody
    public String testCookie(HttpServletResponse response)
    {
        Cookie cookie = new Cookie("user", CommunityUtil.generateUUID());
        cookie.setPath("/cookie"); //设置cookie生效的路径
        cookie.setMaxAge(60*10);   //存活时间
        response.addCookie(cookie);
        return "set-cookie";

    }


    @RequestMapping("/cookie/get")
    @ResponseBody
    public String GetSession(HttpSession session)
    {
        session.setAttribute("user","kunkun");
        return "success";
    }
}
