package com.project.template.vo;

import com.project.template.enums.StateType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class SysUserListVO {

    @ApiModelProperty("id")
    private Integer id;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("创建时间")
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty("个人简介")
    private String content;

    @ApiModelProperty("头像")
    private String img;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("电话")
    private String mobile;

    @ApiModelProperty("地址")
    private String address;

    @ApiModelProperty("角色类型")
    private String roleType;

    @ApiModelProperty("性别")
    private String gender;

    @ApiModelProperty("状态")
    private StateType state;


}
