package com.project.template.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.project.template.dto.EditPasswordDTO;
import com.project.template.dto.SysUserDTO;
import com.project.template.dto.UserLoginDTO;
import com.project.template.dto.UserRegisterDTO;
import com.project.template.entity.SysUser;
import com.project.template.enums.StateType;
import com.project.template.exception.CustomException;
import com.project.template.mapper.SysUserMapper;
import com.project.template.service.SysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.template.utils.JwtHelper;
import com.project.template.utils.RoleMenuUtils;
import com.project.template.utils.UserThreadLocal;
import com.project.template.utils.Utils;
import com.project.template.vo.SysUserLoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Value("${resetPassword}")
    private String resetPassword;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private JwtHelper jwtUtils;

    @Override
    public void register(UserRegisterDTO userRegisterDTO) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, userRegisterDTO.getUsername())
                .last("limit 1");
        //有值 提示用户名已重复
        SysUser userInfo = getOne(wrapper);
        if (userInfo != null) {
            throw new CustomException("用户名称重复");
        }
        SysUser user = new SysUser();
        BeanUtil.copyProperties(userRegisterDTO, user);
        String salt = Utils.salt();
        user.setSalt(salt);
        //密码加密
        user.setPassword(SecureUtil.md5(user.getPassword() + salt));
        // 用户注册需将权限设置为普通用户
        user.setRoleType("USER");
        //账号默认为启用
        user.setState(StateType.ENABLE);
        //注册时间
        user.setCreateTime(new Date());
        saveOrUpdate(user);
    }

    @Override
    public void editPassword(EditPasswordDTO dto) {
        SysUserDTO user = UserThreadLocal.getCurrentUser();
        //先查询用户
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getId, user.getId())
                .last("limit 1");
        SysUser sysUser = getOne(wrapper);
        if (sysUser != null) {
            LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysUser::getPassword, SecureUtil.md5(dto.getPassword() + sysUser.getSalt()))
                    .last("limit 1");
            SysUser currentUser = getOne(queryWrapper);
            if (currentUser != null) {
                //重新生成盐值
                String salt = Utils.salt();
                currentUser.setSalt(salt);
                currentUser.setPassword(SecureUtil.md5(dto.getNewPassword() + salt));
                saveOrUpdate(currentUser);
            } else {
                throw new CustomException("修改失败");
            }
        } else {
            throw new CustomException("用户未查询到");
        }
    }

    /**
     * 登录逻辑
     * @param userLoginDTO
     * @return
     */
    @Override
    public SysUserLoginVO login(UserLoginDTO userLoginDTO) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, userLoginDTO.getUsername())
                .last("limit 1");
        SysUser userInfo = getOne(wrapper);
        String captcha = userLoginDTO.getCaptcha();
        //校验登陆请求参数
        Object code = redisTemplate.opsForValue().get("code");
        if(Objects.isNull(code)){
            throw new CustomException("验证码失效，请重新刷新");
        }
        if (!captcha.equalsIgnoreCase(code.toString())){
            throw new CustomException(("验证码输入错误"));
        }
        if (userInfo != null) {
            String salt = userInfo.getSalt();

            if (!SecureUtil.md5(userLoginDTO.getPassword() + salt).equals(userInfo.getPassword())) {
                throw new CustomException("请检查用户名密码是否正确");
            }
            //检查用户状态是否是启用
            if (!(StateType.ENABLE.toString().equals(userInfo.getState().getName()))) {
                throw new CustomException("当前用户已经被禁用");
            }

            // 登录成功后，删除 Redis 中的验证码
            redisTemplate.delete("code");
            //生成jwt
            String token = jwtUtils.generateToken(userInfo);
            SysUserLoginVO sysUserLoginVO = new SysUserLoginVO();
            BeanUtil.copyProperties(userInfo, sysUserLoginVO);
            sysUserLoginVO.setToken(token);
            //生成菜单
            sysUserLoginVO.setRoleMenu(RoleMenuUtils.getRoleMenu(sysUserLoginVO.getRoleType()));
            return sysUserLoginVO;
        } else {
            throw new CustomException("请检查用户名密码是否正确");
        }

    }

    /**
     * 验证码验证
     * @param username
     * @param captcha
     */
    public void validateCaptcha(String username, String captcha){

    }
//    @Override
//    public void insertOrUpdate(SysUser user) {
//        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(SysUser::getUsername, user.getUsername());
//        wrapper.ne(user.getId() != null, SysUser::getId, user.getId());
//        if (count(wrapper) > 0) {
//            throw new CustomException("用户名重复");
//        }
//        if (user.getId() != null) {
//            String salt = Utils.salt();
//            user.setSalt(salt);
//            //密码加密
//            user.setPassword(SecureUtil.md5(user.getPassword() + salt));
//        }
//        saveOrUpdate(user);
//    }

@Override
public void insertOrUpdate(SysUser user) {
    // 检查用户名是否已存在
    LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(SysUser::getUsername, user.getUsername());
    if (user.getId() != null) {
        // 排除当前正在操作的记录
        wrapper.ne(SysUser::getId, user.getId());
    }

    // 查询是否有重复的用户名
    boolean usernameExists = count(wrapper) > 0;
    if (usernameExists) {
        throw new CustomException("用户名重复");
    }

    // 如果用户ID存在，处理密码加密
    if (user.getId() != null) {
        String salt = Utils.salt();
        user.setSalt(salt);
        // 密码加密
        user.setPassword(SecureUtil.md5(user.getPassword() + salt));
    }

    // 保存或更新用户信息
    saveOrUpdate(user);
}


    @Override
    public String resetPassword(SysUser user) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, user.getUsername());
        wrapper.eq(SysUser::getId, user.getId());
        if (count(wrapper) > 0) {
            SysUser sysUser = getOne(wrapper);
            String salt = Utils.salt();
            sysUser.setSalt(salt);
            //密码加密
            sysUser.setPassword(SecureUtil.md5(resetPassword + salt));
            updateById(sysUser);
            return "重置成功，重置密码为：" + resetPassword;
        } else {
            throw new CustomException("未找到该用户");
        }
    }

    /**
     * 批量导入
     * @param sysUsers
     */
    @Override
    public void insert(List<SysUser> sysUsers) {

        for (SysUser user : sysUsers) {
            // 检查用户名是否已存在
            LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysUser::getUsername, user.getUsername());
            if (user.getId() != null) {
                // 排除当前正在操作的记录
                wrapper.ne(SysUser::getId, user.getId());
            }

            // 查询是否有重复的用户名
            boolean usernameExists = count(wrapper) > 0;
            if (usernameExists) {
                throw new CustomException("用户名重复: " + user.getUsername());
            }

            // 如果用户ID存在，处理密码加密
            if (user.getId() != null && user.getPassword() != null) {
                String salt = Utils.salt();
                user.setSalt(salt);
                // 密码加密
                user.setPassword(SecureUtil.md5(user.getPassword() + salt));
            }

            // 保存或更新用户信息
            saveOrUpdate(user);
        }
    }
}
