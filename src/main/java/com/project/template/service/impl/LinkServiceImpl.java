package com.project.template.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.template.entity.Link;
import com.project.template.mapper.LinkMapper;
import com.project.template.service.LinkService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LinkServiceImpl extends ServiceImpl<LinkMapper,Link>implements LinkService {
    @Override
    public Page<Link> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Link> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Link::getId);
        if(query.get("name")!=null){wrapper.like(Link::getName,query.get("name"));}
        return page(new Page<>(pageNum,pageSize),wrapper);
    }
}
