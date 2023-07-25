package com.kuang.controller;

import com.google.code.kaptcha.Producer;
import com.kuang.Service.UserService;
import com.kuang.entity.User;
import com.kuang.utils.CommunityConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @GetMapping ("/register")
    public String getRegisterPage() {  //跳转到注册页
        return "/site/register";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String getLoginPage() {
        return "/site/login";
    }

    @PostMapping("/register")
    public String register(Model model, User user)
    {
        Map<String, Object> map = userService.register(user);
        if (map == null || map.isEmpty()) {
            map.put("msg", "注册成功,我们已经向您的邮件发送了一封激活邮件,请尽快激活！");
            map.put("target", "/index");
            return "/site/operate-result";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "/site/register";
        }

    }

    @RequestMapping(value = "/activation/{userId}/{code}", method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code) {
        int result = userService.activation(userId, code);
        if (result == ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "激活成功,你的账号已经可以正常使用了！");
            model.addAttribute("target", "/login");
        } else if (result == ACTIVATION_REPEAT) {
            model.addAttribute("msg", "无效操作,该账号已经激活过了！");
            model.addAttribute("target", "/index");
        } else {
            model.addAttribute("msg", "激活失败,你提供的激活码不正确！");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }


    @RequestMapping(value = "/kaptcha", method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response, HttpSession session)
    {
        //生成验证码
        String text = kaptchaProducer.createText();   //"ABCD" 长度为4的字符串
        BufferedImage image = kaptchaProducer.createImage(text);

        //将验证码存入session
        session.setAttribute("kaptcha", text);

        //将图片输出给浏览器
        response.setContentType("image/png");
        try {
            ServletOutputStream os = response.getOutputStream();  //通过流将图片输出到浏览器
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            System.out.println("响应验证码失败:" + e.getMessage());
        }

    }


    @PostMapping("/login")
    public String userLogin(String username, String password,String code, boolean rememberme,
                            Model model, HttpSession session, HttpServletResponse response)
    {
        //验证码之前存到了session中，session里面是真实的验证码
        String kaptcha = (String) session.getAttribute("kaptcha");  //实际的
        if(StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg","验证码不正确");
            return "/site/login";
        }

        //比较账号密码是否正确
        int expiredSeconds = rememberme ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        if (map.containsKey("ticket")) { //登录成功，在cookie里存放一份信息(用户的登录凭证)
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
//            cookie.setPath("");
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);  //cookie中存放的是每个用户的登录凭证（一串随机数）
            return "redirect:/index";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";
        }

    }

    /**
     * 退出登录功能
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/login";
    }


}
