package com.openclassrooms.mddapi.model.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class TopicDto {

    private Integer id;
    private String title;
    private String description;
    private Date createdAt;
    private Date updatedAt;
}