package com.project.template.controller;



import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.template.common.PageVO;
import com.project.template.common.Result;
import com.project.template.entity.Notice;
import com.project.template.entity.SysUser;
import com.project.template.service.NoticeService;
import com.project.template.utils.Utils;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/notice")
public class NoticeController {
    @Resource
    NoticeService noticeService;
    /**
     * 列表
     * @return
     */
    @ApiOperation(value = "用户列表", notes = "")
    @GetMapping("/list")
    public Result<List<Notice>> list() {
        return new Result<>().success(noticeService.list());
    }

    /**
     * 分页查询
     * @param name
     * @param pageNum
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "分页查询", notes = "分页查询")
    @GetMapping("/page")
    public Result<PageVO<Notice>> findPage(@RequestParam(defaultValue = "") String name,
                                           @RequestParam(defaultValue = "1") Integer pageNum,
                                           @RequestParam(defaultValue = "10") Integer pageSize) {
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        //查出的数据根据id降序排列且支持用户名称模糊查询
        wrapper.orderByDesc(Notice::getId).like(StringUtils.isNotBlank(name), Notice::getName, name);
        Page<Notice> page = noticeService.page(new Page<>(pageNum,pageSize),wrapper);
        return new Result<>().success(new PageVO<>(page));
    }

    /**
     * 数据新增
     * @param notice
     * @return
     */
    @ApiOperation(value = "数据新增", notes = "数据新增")
    @PostMapping("/add")
    public Result add(@Validated @RequestBody Notice notice) {
        notice.setUser(Utils.getUser().getUsername());
        notice.setCreateTime(new Date());
        noticeService.save(notice);
        return new Result<>().success();
    }

    /**
     * 数据更新
     * @param notice
     * @return
     */
    @ApiOperation(value = "数据更新", notes = "数据更新")
    @PutMapping("/update")
    public Result updateById(@Validated @RequestBody Notice notice) {
        noticeService.updateById(notice);
        return new Result<>().success();
    }

    /**
     * 根据id删除
     * @param ids
     * @return
     */
    @ApiOperation(value = "数据根据id批量删除", notes = "数据根据id批量删除")
    @DeleteMapping("/delBatch")
    public Result delBatch(@RequestBody List<Integer> ids) {
        noticeService.removeByIds(ids);
        return new Result<>().success();
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @GetMapping("/getById")
    public Result<Notice> getById(@RequestParam("id") Integer id) {
        Notice notice = noticeService.getById(id);
        return new Result<>().success(notice);
    }

}
