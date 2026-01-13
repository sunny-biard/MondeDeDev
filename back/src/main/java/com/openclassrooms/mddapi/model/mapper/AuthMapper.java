package com.openclassrooms.mddapi.model.mapper;

import com.openclassrooms.mddapi.model.response.AuthResponse;

public class AuthMapper {
    
    public static AuthResponse toResponse(String token) {
        AuthResponse response = new AuthResponse();

        response.setToken(token);
        
        return response;
    }
}
