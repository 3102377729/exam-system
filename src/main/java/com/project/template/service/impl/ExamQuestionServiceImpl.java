package com.project.template.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.template.dto.SysUserDTO;
import com.project.template.entity.ExamQuestion;
import com.project.template.enums.RoleType;
import com.project.template.mapper.ExamQuestionMapper;
import com.project.template.service.ExamQuestionService;
import com.project.template.utils.UserThreadLocal;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ExamQuestionServiceImpl extends ServiceImpl<ExamQuestionMapper, ExamQuestion> implements ExamQuestionService {
    @Override
    public Page<ExamQuestion> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<ExamQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(ExamQuestion::getId);

        //老师只能看到自己的试题
        SysUserDTO currentUser = UserThreadLocal.getCurrentUser();

        if(RoleType.TEACHER.getKey().equals(currentUser.getRoleType())){
            wrapper.eq(ExamQuestion::getUserId,currentUser.getId());
        }

        System.out.println(query.get("name123"));
        //条件模糊查询
        if(query.get("name")!=null){
            wrapper.like(ExamQuestion::getName,query.get("name"));
        }
        return page(new Page<>(pageNum,pageSize),wrapper);
    }
}
