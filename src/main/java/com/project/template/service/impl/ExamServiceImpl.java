package com.project.template.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.template.dto.SysUserDTO;
import com.project.template.entity.Exam;
import com.project.template.entity.Score;
import com.project.template.entity.SysUser;
import com.project.template.enums.RoleType;
import com.project.template.mapper.ExamMapper;
import com.project.template.service.ExamService;
import com.project.template.service.ScoreService;
import com.project.template.service.SysUserService;
import com.project.template.utils.UserThreadLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class ExamServiceImpl extends ServiceImpl<ExamMapper, Exam> implements ExamService {

    @Autowired
    private SysUserService userService;

    @Resource
    private ScoreService scoreService;
    @Override
    public Page<Exam> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Exam> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Exam::getId);
        //条件模糊查询
        if(query.get("name")!=null){
            wrapper.like(Exam::getName,query.get("name"));
        }

        SysUserDTO currentUser = UserThreadLocal.getCurrentUser();
        //每个老师只能看到自己发布的试卷
        if(RoleType.TEACHER.getKey().equals(currentUser.getRoleType())){
            wrapper.eq(Exam::getUserId,currentUser.getId());
        }
        Page<Exam> page = page(new Page<>(pageNum, pageSize), wrapper);
        List<Exam> list = page.getRecords();
        //判断用户有没有参考考试
//        通过开始结果去查
        list.forEach(item->{
            SysUser user = userService.getById(item.getUserId());
            if(user!=null){
                item.setUser(user.getUsername());
            }
            LambdaQueryWrapper<Score> scoreLambdaQueryWrapper = new LambdaQueryWrapper<>();
            scoreLambdaQueryWrapper.eq(Score::getUserId,currentUser.getId())
                    .eq(Score::getExamId,item.getId())
                    .last("limit 1");
            Score one = scoreService.getOne(scoreLambdaQueryWrapper);
            if(one!=null){
                item.setIsExam("已考试");
            }else{
                item.setIsExam("立即考试");
            }
        });
        return page;
    }
}
