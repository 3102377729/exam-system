package com.project.template.controller;


import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.project.template.model.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * 验证码操作处理
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/cap")
@CrossOrigin
public class CaptchaController
{
    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping
    public ResponseResult getCaptcha(){
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(80, 25, 4, 10);
        //得到验证码
        String code = lineCaptcha.getCode();
        //存储到redis
        redisTemplate.opsForValue().set("code",code);
        //设置过期时间
        redisTemplate.expire("code",60, TimeUnit.SECONDS);
        //返回base64的图片
        String imageBase64Data = lineCaptcha.getImageBase64Data();
        return new ResponseResult<>(200,"success",imageBase64Data);
    }

}
