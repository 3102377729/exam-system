package com.project.template.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;
@Data
public class Exam extends BaseEntity{


    private String name;

    private String img;

    private Date startTime;

    private Date endTime;

    private String question;

    private Integer score;

    private Integer userId;

    @TableField(exist = false)
    private String isExam;

    @TableField(exist = false)
    private String user;
}
