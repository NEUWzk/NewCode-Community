package com.kuang;
import com.kuang.dao.LoginTicketMaper;
import com.kuang.entity.LoginTicket;
import com.kuang.utils.MailClient;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@SpringBootTest
class NewCoderApplicationTests {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private LoginTicketMaper loginTicketMaper;

    private static final Logger logger = LoggerFactory.getLogger(NewCoderApplicationTests.class);

    @Test
    void contextLoads() {

        loginTicketMaper.updateStatus("123",1);

    }


}
