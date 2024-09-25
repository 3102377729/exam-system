package com.project.template.service;

import com.project.template.dto.EditPasswordDTO;
import com.project.template.dto.UserLoginDTO;
import com.project.template.dto.UserRegisterDTO;
import com.project.template.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.project.template.vo.SysUserLoginVO;

import java.util.List;

public interface SysUserService extends IService<SysUser> {

    void register(UserRegisterDTO userRegisterDTO);

    void editPassword(EditPasswordDTO dto);

    SysUserLoginVO login(UserLoginDTO userLoginDTO);

    void insertOrUpdate(SysUser user);

    String resetPassword(SysUser user);

    void insert(List<SysUser> sysUsers);
}
