package com.example.authentication.dto.user;

import com.example.authentication.validation.password.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with")
public class UserResetPasswordDto {
    @NotBlank
    @ValidPassword
    private String newPassword;
    @NotBlank
    @ValidPassword
    private String matchingPassword;
}
