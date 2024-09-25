package com.project.template.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.project.template.entity.Banner;
import com.project.template.entity.Score;

import java.util.Map;

public interface ScoreService extends IService<Score> {
    Page<Score> page(Map<String, Object> query, Integer pageNum, Integer pageSize);
}
