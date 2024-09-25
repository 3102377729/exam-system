package com.project.template.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.template.dto.SysUserDTO;
import com.project.template.entity.Score;
import com.project.template.entity.SysUser;
import com.project.template.enums.RoleType;
import com.project.template.mapper.ScoreMapper;
import com.project.template.service.ScoreService;
import com.project.template.service.SysUserService;
import com.project.template.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ScoreServiceImpl extends ServiceImpl<ScoreMapper, Score> implements ScoreService {
   @Autowired
    private SysUserService userService;

    @Override
    public Page<Score> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Score> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Score::getId);
        //条件模糊查询
        if(query.get("name")!=null){
            wrapper.like(Score::getName,query.get("name"));
        }

        //老师能看到全部有关联得试卷
        SysUserDTO user = Utils.getUser();
        if(RoleType.TEACHER.getKey().equals(user.getRoleType())){
            wrapper.eq(Score::getTeacherId,user.getId());
        }

        if(RoleType.USER.getKey().equals(user.getRoleType())){
            wrapper.eq(Score::getUserId,user.getId());
        }

        Page<Score> page = page(new Page<>(pageNum, pageSize), wrapper);
        //学生只能看到自己的试卷

        //获取用户名
        page.getRecords().forEach(item->{
            SysUser sysUser = userService.getById(item.getUserId());
            if(sysUser!=null){
                item.setUser(sysUser.getUsername());
            }
        });

        return page;
    }
}