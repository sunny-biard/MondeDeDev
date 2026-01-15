package com.openclassrooms.mddapi.model.dto;

import java.sql.Date;
import java.util.List;

import lombok.Data;

@Data
public class UserProfileDto {

    private Integer id;
    private String username;
    private String email;
    private Date createdAt;
    private Date updatedAt;
    private List<TopicDto> subscriptions;
}