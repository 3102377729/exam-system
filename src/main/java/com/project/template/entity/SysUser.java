package com.project.template.entity;

import cn.hutool.core.annotation.Alias;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.project.template.enums.StateType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Getter
@Setter
@TableName("sys_user")
@ApiModel(value = "User对象", description = "用户信息表")
public class SysUser extends BaseEntity {

    //数据为""和" "和null的情况下会被拦截
    @NotBlank(message = "用户名不能为空")
//    @Length(min = 5,max = 50)
    @Length(min = 2,max = 15,message = "用户名长度需要在[2和15]之间")
    @ApiModelProperty("用户名")
    @TableField("username")

    private String username;

//    @NotBlank(message = "密码不能为空")
    @Length(min = 2,max = 15,message = "密码需要在[2和15]之间")
    @ApiModelProperty("密码")

    private String password;

    @ApiModelProperty("个人简介")

    private String content;

    @ApiModelProperty("邮箱")

    private String email;

    @ApiModelProperty("电话")

    private String mobile;

    @ApiModelProperty("地址")

    private String address;

    @ApiModelProperty("头像")

    private String img;

    @ApiModelProperty("角色类型")

    private String roleType;

    @ApiModelProperty("加盐")

    private String salt;

    @ApiModelProperty("性别")

    private String gender;

    @ApiModelProperty("状态 启用/禁用")

    @TableField("state")
    private StateType state;

    @ApiModelProperty("创建时间")

    private Date createTime;

    @ApiModelProperty("更新时间")

    private Date updateTime;

}
