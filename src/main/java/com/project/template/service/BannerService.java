package com.project.template.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.project.template.entity.Banner;
import com.project.template.mapper.BannerMapper;

import java.util.Map;

public interface BannerService extends IService<Banner> {
    Page<Banner> page(Map<String, Object> query, Integer pageNum, Integer pageSize);
}
