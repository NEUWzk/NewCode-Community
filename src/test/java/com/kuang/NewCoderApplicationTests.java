package com.kuang;

import com.kuang.Service.DiscussPostService;
import com.kuang.dao.DiscussPostMapper;
import com.kuang.dao.UserMapper;
import com.kuang.entity.DiscussPost;
import com.kuang.entity.User;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
class NewCoderApplicationTests {

    @Resource
    private UserMapper userMapper;

    @Resource
    private DiscussPostMapper discussPostMapper;

    private static final Logger logger = LoggerFactory.getLogger(NewCoderApplicationTests.class);

    @Test
    void contextLoads() {
       logger.debug("hello");
    }

}
