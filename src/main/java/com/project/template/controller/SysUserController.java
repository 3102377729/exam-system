package com.project.template.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.template.common.PageVO;
import com.project.template.common.Result;
import com.project.template.dto.*;
import com.project.template.entity.ExamQuestion;
import com.project.template.entity.SysUser;
import com.project.template.enums.RoleType;
import com.project.template.service.SysUserService;
import com.project.template.utils.Utils;
import com.project.template.vo.SysUserListVO;
import com.project.template.vo.SysUserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Api(value = "用户模块", tags = "用户模块")

@RestController
@RequestMapping("/sysUser")
@Slf4j
public class SysUserController{

    @Resource
    SysUserService sysUserService;

    /**
     * 列表
     * @return
     */
    @ApiOperation(value = "用户列表", notes = "")
    @GetMapping("/list")
    public Result<List<SysUser>> list() {
        return new Result<>().success(sysUserService.list());
    }

    /**
     * 分页查询
     * @param
     * @param pageNum
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "分页查询", notes = "分页查询")
    @GetMapping("/page")
    public Result<PageVO<SysUser>> findPage(@RequestParam(defaultValue = "") String user,
                                            @RequestParam(defaultValue = "1") Integer pageNum,
                                            @RequestParam(defaultValue = "10") Integer pageSize) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        //查出的数据根据id降序排列
        wrapper.orderByDesc(SysUser::getId);

        Page<SysUser> page = sysUserService.page(new Page<>(pageNum,pageSize),wrapper);
        Page<SysUserListVO> voPage = new Page<>();
        BeanUtil.copyProperties(page,voPage);
        List<SysUser> records = page.getRecords();
        List<SysUserListVO> listVOList = new ArrayList<>();
        records.forEach(item->{
            SysUserListVO sysUserListVO = new SysUserListVO();
            BeanUtil.copyProperties(item,sysUserListVO);
            listVOList.add(sysUserListVO);
        });
        voPage.setRecords(listVOList);
        return new Result<>().success(new PageVO<>(voPage));
    }

    /**
     * 数据新增
     * @param user
     * @return
     */
    @ApiOperation(value = "数据新增", notes = "数据新增")
    @PostMapping("/add")
    public Result add(@Validated @RequestBody SysUser user) {
        sysUserService.insertOrUpdate(user);
        return new Result<>().success();
    }

    /**
     * 数据更新
     * @param user
     * @return
     */
    @ApiOperation(value = "数据更新", notes = "数据更新")
    @PutMapping("/update")
    public Result updateById(@Validated @RequestBody SysUser user) {
        sysUserService.insertOrUpdate(user);
        return new Result<>().success();
    }

    /**
     * 根据id删除
     * @param ids
     * @return
     */
    @ApiOperation(value = "数据根据id批量删除", notes = "数据根据id批量删除")
    @DeleteMapping("/delBatch/{ids}")
    public Result delBatch(@PathVariable List<Integer> ids) {
        sysUserService.removeByIds(ids);
        return new Result<>().success();
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @GetMapping("/getById")
    public Result<SysUserVO> getById(@RequestParam("id") Integer id) {
        SysUser byId = sysUserService.getById(id);
        SysUserVO userVO = new SysUserVO();
        BeanUtils.copyProperties(byId, userVO);
        return new Result<>().success(userVO);
    }

    /**
     * 用户登录
     * @param userLoginDTO
     * @return
     */
    @PostMapping("/login")
    public Result login(@Validated @RequestBody UserLoginDTO userLoginDTO) {
        return new Result<>().success(sysUserService.login(userLoginDTO));
    }

    /**
     * 用户注册
     * @param userRegisterDTO
     * @return
     */
    @PostMapping("/register")
    public Result register(@Validated @RequestBody UserRegisterDTO userRegisterDTO) {
        sysUserService.register(userRegisterDTO);
        return new Result<>().success();
    }

    /**
     * 修改密码
     * @param dto
     * @return
     */
    @PutMapping("/editPassword")
    public Result editPassword(@RequestBody EditPasswordDTO dto){
        sysUserService.editPassword(dto);
        return new Result<>().success();
    }

    /**
     * 数据更新
     * @param user
     * @return
     */
    @ApiOperation(value = "密码重置", notes = "密码重置")
    @PutMapping("/resetPassword")
    public Result resetPassword(@RequestBody SysUser user) {
        return new Result<>().success(sysUserService.resetPassword(user));
    }

    /**
     *批量导出数据
     * @param username
     */
    @GetMapping("/export")
    @ApiOperation("导出用户数据报表")
    public void export(@RequestParam(required = false) String username,
                       HttpServletResponse response) throws IOException {
        // 创建 ExcelWriter 实例
        ExcelWriter writer = ExcelUtil.getWriter(true);

        // 查询数据
        List<SysUser> list;
        if (StrUtil.isBlank(username)) {
            // 查询所有数据
            list = sysUserService.list();
        } else {
            QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
            queryWrapper.like("username", username);
            // 根据用户名查询数据
            list = sysUserService.list(queryWrapper);
        }

        // 写入数据
        writer.write(list, true);

        // 设置响应格式
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("用户信息表", "UTF-8") + ".xlsx");

        // 输出文件
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            writer.flush(outputStream, true);
        } finally {
            writer.close();
        }
    }

    /**
     * 批量导入
     * @param file
     */
    @PostMapping("/import")
    @ApiOperation("导入用户数据报表")
    public Result importData(MultipartFile file) throws IOException {

//        ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
//        List<SysUser> sysUsers = reader.readAll(SysUser.class);
//
//        //写入数据库
//        sysUserService.insert(sysUsers);
//        return new Result().success();
//    }

        try (InputStream inputStream = file.getInputStream()) {
            ExcelReader reader = ExcelUtil.getReader(inputStream);
            List<SysUser> sysUsers = reader.readAll(SysUser.class);
            // 写入数据库
            sysUserService.insert(sysUsers);
            log.info("Successfully imported {} users.", sysUsers.size());
            return new Result().success();
        } catch (IOException e) {
            log.error("Failed to import data", e);
            return new Result().error("数据导入失败，请检查文件格式或内容。");
        } catch (Exception e) {
            log.error("Unexpected error during import", e);
            return new Result().error("数据导入过程中发生未知错误。");
        }
    }

}
