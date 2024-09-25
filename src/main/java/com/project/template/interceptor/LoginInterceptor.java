package com.project.template.interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.project.template.dto.SysUserDTO;
import com.project.template.entity.SysUser;
import com.project.template.exception.CustomException;
import com.project.template.service.impl.SysUserServiceImpl;
import com.project.template.utils.JwtHelper;
import com.project.template.utils.UserThreadLocal;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private SysUserServiceImpl sysUserService;
   @Autowired
    private JwtHelper jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 打印请求信息
        log.info("------------- LoginInterceptor 开始 -------------");
        long startTime = System.currentTimeMillis();
        request.setAttribute("requestStartTime", startTime);

        // OPTIONS请求不做校验,
        // 前后端分离的架构, 前端会发一个OPTIONS请求先做预检, 对预检请求不做校验
        if(request.getMethod().toUpperCase().equals("OPTIONS")){
            return true;
        }

        String path = request.getRequestURL().toString();
        log.info("接口登录拦截：，path：{}", path);

        //获取header的token参数
        String token = request.getHeader("token");
        log.info("登录校验开始，token：{}", token);
//        if(StrUtil.isBlank(token)){
//            token=request.getParameter("token");//url参数  ?token=xxx
//        }
        if (token == null || token.isEmpty()) {
            log.info( "token为空，请求被拦截" );
            response.setStatus(HttpStatus.UNAUTHORIZED.value());

            return false;
        }


        Claims claims = jwtUtils.verifyJwt(token);
        //获取用户ID
        if (claims == null) {
            log.warn( "token无效，请求被拦截" );
            throw new CustomException(401,"token无效，请求被拦截");
        } else {
            Integer userId = (Integer)claims.get("userId");
            SysUser byId = sysUserService.getById(userId);
            if(byId != null){

//                if(!RoleType.ROLE_ADMIN.equals(byId.getRoleType())){
//                    if (!Utils.isRole(path,byId.getRoleType().getKey())) {
//                        throw new CustomException(401,"该接口无访问权限");
//                    }
//                }

                //输出
                SysUserDTO sysUserDTO = new SysUserDTO();
                BeanUtil.copyProperties(byId,sysUserDTO);
                UserThreadLocal.set(sysUserDTO);

            }
            log.info("已登录：{}", userId);

            return true;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        long startTime = (Long) request.getAttribute("requestStartTime");
        log.info("------------- LoginInterceptor 结束 耗时：{} ms -------------", System.currentTimeMillis() - startTime);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        UserThreadLocal.clear();
        log.info("LogInterceptor 结束");
    }
}