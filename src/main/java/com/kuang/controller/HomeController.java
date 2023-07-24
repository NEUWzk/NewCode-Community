package com.kuang.controller;

import com.kuang.Service.DiscussPostService;
import com.kuang.Service.UserService;
import com.kuang.entity.DiscussPost;
import com.kuang.entity.Page;
import com.kuang.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/index",method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page)
    {
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");

        //orderMode == 0 :直接按照时间降序排列
        List<DiscussPost> list =
                discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit(), 0);
        List<Map<String,Object>> discussPosts = new ArrayList<>(); //存放最终结果 {},{},{}...
        if (list!=null)
        {
            for(DiscussPost post:list) {  //post代表查询结果的每一项，都是一个DiscussPost对象 （xx,xx...）
                HashMap<String, Object> map = new HashMap<>();

                map.put("post", post);
                //将发布帖子对应的用户id作为参数
                User user = userService.fingUserById(post.getUserId());
                //将发帖子的所有用户放入map
                map.put("user", user);
                discussPosts.add(map);
            }  //{}，{}，{}...
        }
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("page",page);
        return "/index";
    }

}
