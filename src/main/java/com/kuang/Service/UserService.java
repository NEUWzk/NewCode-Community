package com.kuang.Service;

import com.kuang.dao.UserMapper;
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
    public int activation(int userId, String code) {
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



}


