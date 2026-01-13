package com.openclassrooms.mddapi.model.mapper;

import java.sql.Date;

import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.openclassrooms.mddapi.model.dto.UserDto;
import com.openclassrooms.mddapi.model.entity.User;
import com.openclassrooms.mddapi.model.request.RegisterRequest;

public class UserMapper {
    
    @NonNull
    public static User toEntity(RegisterRequest req, PasswordEncoder passwordEncoder) {
        User user = new User();

        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setCreatedAt(new Date(System.currentTimeMillis()));
        user.setUpdatedAt(new Date(System.currentTimeMillis()));

        return user;
    }
    
    @NonNull
    public static UserDto toDto(User user) {
        UserDto userDto = new UserDto();

        userDto.setEmail(user.getEmail());
        userDto.setUsername(user.getActualUsername());
        userDto.setCreatedAt(user.getCreatedAt());
        userDto.setUpdatedAt(user.getUpdatedAt());
        
        return userDto;
    }  
}