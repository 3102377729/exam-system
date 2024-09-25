package com.project.template.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.project.template.entity.ExamQuestion;

import java.util.Map;

public interface ExamQuestionService extends IService<ExamQuestion> {
    Page<ExamQuestion> page(Map<String, Object> query, Integer pageNum, Integer pageSize);
}
