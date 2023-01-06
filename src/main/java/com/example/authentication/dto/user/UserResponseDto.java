package com.example.authentication.dto.user;

import com.example.authentication.entities.enums.Role;
import com.example.authentication.entities.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with")
public class UserResponseDto {
    private Long id;
    private String email;
    private Role role;
    private Status status;
}
