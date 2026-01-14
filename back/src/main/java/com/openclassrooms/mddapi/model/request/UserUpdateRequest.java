package com.openclassrooms.mddapi.model.request;

import lombok.Data;

@Data
public class UserUpdateRequest {

    private String username;
    private String email;
    private String password;
}