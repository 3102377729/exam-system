package com.project.template.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.template.entity.Banner;
import com.project.template.mapper.BannerMapper;
import com.project.template.service.BannerService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements BannerService {
    @Override
    public Page<Banner> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Banner::getId);
        System.out.println(query.get("name123"));
        //条件模糊查询
        if(query.get("name")!=null){
            wrapper.like(Banner::getName,query.get("name"));
        }
        return page(new Page<>(pageNum,pageSize),wrapper);
    }
}