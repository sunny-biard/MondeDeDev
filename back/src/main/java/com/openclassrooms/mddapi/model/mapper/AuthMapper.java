package com.openclassrooms.mddapi.model.mapper;

import com.openclassrooms.mddapi.model.response.AuthSuccess;

public class AuthMapper {
    
    public static AuthSuccess toResponse(String token) {
        AuthSuccess response = new AuthSuccess();
        response.setToken(token);
        return response;
    }
}
