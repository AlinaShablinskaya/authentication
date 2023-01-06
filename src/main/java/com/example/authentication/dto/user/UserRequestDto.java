package com.example.authentication.dto.user;

import com.example.authentication.validation.email.ValidEmail;
import com.example.authentication.validation.password.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with")
public class UserRequestDto {
    @NotBlank
    @Size(max = 65)
    @ValidEmail
    private String email;
    @NotBlank
    @ValidPassword
    private String password;
}
