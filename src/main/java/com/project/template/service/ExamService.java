package com.project.template.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.project.template.entity.Exam;

import java.util.Map;

public interface ExamService extends IService<Exam> {
    Page<Exam> page(Map<String, Object> query, Integer pageNum, Integer pageSize);
}
