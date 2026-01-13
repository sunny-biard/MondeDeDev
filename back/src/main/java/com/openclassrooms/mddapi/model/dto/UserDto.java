package com.openclassrooms.mddapi.model.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class UserDto {

    private String username;
    private String email;
    private Date createdAt;
    private Date updatedAt;
}
