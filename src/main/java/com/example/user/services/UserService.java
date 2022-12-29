package com.example.user.services;

import com.example.user.dto.user.UserRequestDto;
import com.example.user.dto.user.UserResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    UserResponseDto registerUser(UserRequestDto userRequestDto);

    UserResponseDto findUserById(Long id);

    List<UserResponseDto> findAll(Pageable pageable);

    void delete(Long id);
}
