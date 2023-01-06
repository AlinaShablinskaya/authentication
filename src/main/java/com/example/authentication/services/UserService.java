package com.example.authentication.services;

import com.example.authentication.dto.user.UserChangePasswordDto;
import com.example.authentication.dto.user.UserForgotPasswordDto;
import com.example.authentication.dto.user.UserRequestDto;
import com.example.authentication.dto.user.UserResetPasswordDto;
import com.example.authentication.dto.user.UserResponseDto;
import com.example.authentication.entities.User;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    UserResponseDto registerUser(UserRequestDto userRequestDto);

    User getCurrentUser();

    UserResponseDto findUserById(Long id);

    User getUserByMailIfExist(String email);

    User findUserByVerificationToken(String token);

    void verifyUserAccount(String token);

    void forgotPassword(UserForgotPasswordDto userForgotPasswordDto);

    void resetPasswordByEmail(UserResetPasswordDto userResetPasswordDto, String token);

    void updatePassword(UserChangePasswordDto userChangePasswordDto);

    List<UserResponseDto> findAll(Pageable pageable);
}
