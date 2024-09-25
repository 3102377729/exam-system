package com.project.template.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.template.entity.News;
import com.project.template.mapper.NewsMapper;
import com.project.template.service.NewsService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NewsServiceImpl extends ServiceImpl<NewsMapper, News> implements NewsService {
    @Override
    public Page<News> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<News> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(News::getId);
        //条件模糊查询
        if(query.get("name")!=null){
            wrapper.like(News::getName,query.get("name"));
        }
        return page(new Page<>(pageNum,pageSize),wrapper);
    }
}