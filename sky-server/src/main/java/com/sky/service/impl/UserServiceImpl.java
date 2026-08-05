package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.sky.constant.MessageConstant;
import com.sky.constant.WeChatConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    
    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private UserMapper userMapper;
    
    // 微信登录服务接口地址
    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";
    
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO){
        // 调用微信接口服务， 获取当前微信用户的openID
        String openid = getOpenid(userLoginDTO.getCode());
        // 判断openId是否为空，如果为空表示登录失败， 抛出业务异常
        if(openid == null || openid == "")
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        
        // 判断当前用户是否为新用户
        User user = userMapper.getByOpenid(openid);
        // 如果是新用户，则自动完成注册
        if(user == null){
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
//            log.info("微信注册：{}", user);
        }
        // 返回这个用户对象
//        log.info("login successfully: {}", user);
        return user;
    }
    
    /**
     * 调用微信接口服务，获取用户openid
     * @param code
     * @return
     */
    private String getOpenid(String code){
        Map<String, String> map = new HashMap();
        map.put(WeChatConstant.APP_ID, weChatProperties.getAppid());
        map.put(WeChatConstant.SECRET, weChatProperties.getSecret());
        map.put(WeChatConstant.JS_CODE, code);
        map.put(WeChatConstant.GRANT_TYPE, WeChatConstant.AUTHORIZATION_CODE);
        
        // 请求并获取返回结果
        String json = HttpClientUtil.doGet(WX_LOGIN,map);
        // 解析请求返回结果
        String openid = JSON.parseObject(json).getString(WeChatConstant.OPEN_ID);
        return openid;
    }
}
