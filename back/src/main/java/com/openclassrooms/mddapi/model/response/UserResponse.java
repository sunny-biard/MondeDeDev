package com.openclassrooms.mddapi.model.response;

import java.sql.Date;

import lombok.Data;

@Data
public class UserResponse {

    private Integer id;
    private String username;
    private String email;
    private Date createdAt;
    private Date updatedAt;
}
