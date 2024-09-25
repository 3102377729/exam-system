package com.project.template.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class Link extends BaseEntity{
    private String name;

    private String link;
}
