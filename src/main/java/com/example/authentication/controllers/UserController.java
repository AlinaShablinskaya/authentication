package com.example.authentication.controllers;

import com.example.authentication.dto.user.UserChangePasswordDto;
import com.example.authentication.dto.user.UserForgotPasswordDto;
import com.example.authentication.dto.user.UserRequestDto;
import com.example.authentication.dto.user.UserResetPasswordDto;
import com.example.authentication.dto.user.UserResponseDto;
import com.example.authentication.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/registration")
    public UserResponseDto registerUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        return userService.registerUser(userRequestDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/verify")
    public void verifyUserAccount(@RequestParam(value = "token") String token) {
        userService.verifyUserAccount(token);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto findById(@PathVariable(value = "id") Long id) {
        return userService.findUserById(id);
    }

    @PostMapping("/forgot")
    @ResponseStatus(HttpStatus.OK)
    public void forgotPassword(@RequestBody UserForgotPasswordDto forgotPasswordDto) {
        userService.forgotPassword(forgotPasswordDto);
    }

    @PostMapping("/recover")
    @ResponseStatus(HttpStatus.OK)
    public void resetPasswordByEmail(@RequestBody UserResetPasswordDto resetPasswordDto,
                                     @RequestParam(value = "token") String token) {
        userService.resetPasswordByEmail(resetPasswordDto, token);
    }

    @PostMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    public void updatePassword(@RequestBody UserChangePasswordDto changePasswordDto) {
        userService.updatePassword(changePasswordDto);
    }
}
