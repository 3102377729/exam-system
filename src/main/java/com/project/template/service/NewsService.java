package com.project.template.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.project.template.entity.News;

import java.util.Map;

public interface NewsService extends IService<News> {
        Page<News> page(Map<String, Object> query, Integer pageNum, Integer pageSize);
}
