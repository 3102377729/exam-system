package com.project.template.utils;

import cn.hutool.core.util.StrUtil;
import com.project.template.config.JwtProperties;
import com.project.template.entity.SysUser;
import com.project.template.service.SysUserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@Component
public class JwtHelper {

    @Autowired
    private  JwtProperties jwtProperties;

    @Resource
    private SysUserService userService;

    /**
     * 创建JWT
     */
    public  String createJWT(Map<String, Object> claims, Long time) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        Date now = new Date(System.currentTimeMillis());

        SecretKey secretKey = generalKey();
        long nowMillis = System.currentTimeMillis();
        JwtBuilder builder = Jwts.builder()
                .setClaims(claims)
                .setId(jwtProperties.getIssuer())
                .setIssuedAt(now)
                .signWith(signatureAlgorithm, secretKey);
        if (time >= 0) {
            long expMillis = nowMillis + time;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }
        return builder.compact();
    }

    /**
     * 验证jwt
     */
    public  Claims verifyJwt(String token) {
        SecretKey key = generalKey();
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token).getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    /**
     * 由字符串生成加密key
     *
     * @return
     */
    public  SecretKey generalKey() {
        String stringKey = jwtProperties.getSecret();
        byte[] encodedKey = Base64.getEncoder().encode(stringKey.getBytes());
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        return key;
    }

    /**
     * 根据userId生成token
     * 传入User实体类
     */
    public  String generateToken(SysUser user) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", user.getId());
        map.put("username", user.getUsername());
        map.put("roleType", user.getRoleType());
        map.put("email", user.getEmail());
        return createJWT(map, jwtProperties.getExpire());
    }

    /**
     * 获取当前登录用户详细信息
     *
     * @return user对象
     */
    public SysUser getCurrentUserInfo() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token = request.getHeader("token");
            if (StrUtil.isNotBlank(token)) {
                Claims claims = verifyJwt(token);
                if (claims != null) {
                    String userId = (String) claims.get("userId");
                    return userService.getById(userId);
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    /**
     * 通过token获取用户Id
     *
     * @param request
     */
    public Integer getUserId(HttpServletRequest request) {
        String token = request.getHeader("token");
        Claims claims = verifyJwt(token);
        if (claims != null) {
            return (Integer) claims.get("userId");
        } else {
            return 0;
        }
    }
}
