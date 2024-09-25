package com.project.template.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.project.template.entity.Link;

import java.util.Map;

public interface LinkService extends IService<Link> {
    Page<Link> page(Map<String, Object> query, Integer pageNum, Integer pageSize);
}
