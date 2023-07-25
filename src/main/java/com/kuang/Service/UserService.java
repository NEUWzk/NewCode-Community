package com.kuang.Service;

import com.kuang.dao.LoginTicketMaper;
import com.kuang.dao.UserMapper;
import com.kuang.entity.LoginTicket;
import com.kuang.entity.User;
import com.kuang.utils.CommunityConstant;
import com.kuang.utils.CommunityUtil;
import com.kuang.utils.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LoginTicketMaper loginTicketMaper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("http://localhost:8080")
    private String domain;  //域名

    @Value("")
    private String contextPath;  //项目的访问前缀路径


    public User fingUserById(int id)
    {
        return userMapper.selectById(id);
    }

    public Map<String, Object> register(User user)  //注册,返回值为map
    {
        HashMap<String, Object> map = new HashMap<>();
        if(user == null)
        {
            throw new IllegalArgumentException("参数不能为空！");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账户不能为空");
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空");
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空");
        }

        User u = userMapper.selectByName(user.getUsername());  //根据输入的信息在数据库查询

        if (u != null) {
            map.put("usernameMsg", "该账号已存在！");
            return map;
        }

        u = userMapper.selectByEmail(user.getEmail());
        if (u != null) {
            map.put("emailMsg", "该邮箱已被注册！");
            return map;
        }

        //验证完毕，接下来需要将注册信息写入数据库：
        //由于传进来的User只包含姓名，邮箱，密码，因此需要手动设置其余的字段默认值
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));  //UUID
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setStatus(0);
        user.setType(0);
        user.setCreateTime(new Date());

        userMapper.insertUser(user);  //在这里mybatis会自动添加ID

        //下面是整合了thymeleaf邮件模板
        Context context = new Context();
        String url = domain + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("email",user.getEmail());  //会被送到activate.html下面进行解析
        context.setVariable("url",url);

        //指定要解析的Html文件,这个模板就是一个激活账户的邮件模板
        String content = templateEngine.process("/mail/activation", context);

        mailClient.sendMail(user.getEmail(), "牛客网激活账号", content); //发送邮件
        return map;
    }

    /**
     * 激活邮件功能
     **/
    public int activation(int userId, String code) {  //邮件模板中的url包含userid以及验证码
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1) {  //status=1表示用户已经激活过了
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            userMapper.updateStatus(userId, 1);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }


    //用户登录业务
    public Map<String, Object> login(String username, String password, int expiredSeconds)
    {
        HashMap<String, Object> map = new HashMap<>();
        if(StringUtils.isBlank(username)) {
            map.put("usernameMsg", "用户名不能为空！");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }

        //在user表里查询用户信息是否正确
        User user = userMapper.selectByName(username);
        if(user == null)
        {
            map.put("usernameMsg", "该账号不存在,请先注册！！");
            return map;
        }
        int userStatus = user.getStatus(); // 用户状态 0 or 1
        if(userStatus == 0){
            map.put("usernameMsg", "该账号未激活！");
            return map;
        }

        password = CommunityUtil.md5(password + user.getSalt());  //加密再比较
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "密码输入错误,请认真检查!！");
            return map;
        }  //执行到这里说明登录成功了
        //接下来需要生成一个登陆成功凭证

        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());  //ticket为一串随机数
        loginTicket.setExpired(new Date(System.currentTimeMillis() +
                expiredSeconds * 1000)); //当前时间的毫秒数+过期时间毫秒数
        loginTicket.setStatus(0);  //status为0，代表有效，可以访问其他业务，退出登录时需要将状态改成1
        loginTicketMaper.insertLoginTicket(loginTicket);

        map.put("ticket", loginTicket.getTicket());

        return map;


    }


    public void logout(String ticket) {
        loginTicketMaper.updateStatus(ticket,1); //状态为1表示无效登录
    }

    public LoginTicket findLoginTicket(String ticket)
    {
        return loginTicketMaper.selectByTicket(ticket);
    }
}


