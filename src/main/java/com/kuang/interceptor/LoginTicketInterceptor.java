package com.kuang.interceptor;

import com.kuang.Service.UserService;
import com.kuang.entity.LoginTicket;
import com.kuang.entity.User;
import com.kuang.utils.CookieUtil;
import com.kuang.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //在Controller之前执行：
        String ticket = CookieUtil.getValue(request, "ticket");
        if(ticket !=null){
            LoginTicket loginTicket = userService.findLoginTicket(ticket);  //根据ticket查询登录记录
            if(loginTicket != null && loginTicket.getStatus() == 0 &&
                    loginTicket.getExpired().after(new Date())){
                User user = userService.fingUserById(loginTicket.getUserId());  //根据session中的ticket凭证查询userId

                //存放到ThreadLocal中,线程隔离
                hostHolder.setUser(user);

            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //在Controller之后执行：
        User user = hostHolder.getUser();
        if(user != null && modelAndView !=null)
        {
            modelAndView.addObject("loginUser",user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //在模板引擎之后执行：
        //释放线程资源
        hostHolder.clear();
    }
}
