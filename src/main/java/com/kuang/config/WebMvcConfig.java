package com.kuang.config;

import com.kuang.interceptor.AlphaInterceptor;
import com.kuang.interceptor.LoginTicketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AlphaInterceptor alphaInterceptor;

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(alphaInterceptor)
                .addPathPatterns("/register", "/login")
                .excludePathPatterns("/**/*/.css","/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");

        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/**/*/.css","/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");
    }
}
