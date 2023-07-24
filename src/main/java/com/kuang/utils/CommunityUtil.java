package com.kuang.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.UUID;


public class CommunityUtil {   //登录时用的工具类,全部为静态方法

    /*生成随机字符串
     * 用于邮件激活码，salt5位随机数加密
     * */
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /*MD5加密
     *hello-->abc123def456 太简单，容易被破解
     *hello + 3e4a8-->abc123def456abc  （加上了随机的字符串，安全性更高）
     */
    public static String md5(String key) {
        if (StringUtils.isBlank(key)) {  //commons下面的StringUtils类
            return null;
        }
        //MD5加密方法
        return DigestUtils.md5DigestAsHex(key.getBytes());
        //参数是bytes型
    }

    /**
     * 标准的返回格式(带3个参数)
     * 方便前后端交互
     */
    public static String getJSONString(int code, String msg, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);

        if (map != null) {
            //从map里的key集合中取出每一个key
            for (String key : map.keySet()) {
                json.put(key, map.get(key));
            }
        }
        return json.toJSONString();
    }

    public static String getJSONString(int code, String msg) {
        return getJSONString(code, msg, null);
    }

    public static String getJSONString(int code) {
        return getJSONString(code, null, null);
    }

}
