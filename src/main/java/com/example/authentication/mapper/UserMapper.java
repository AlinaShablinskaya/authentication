package com.example.authentication.mapper;

import com.example.authentication.dto.user.UserRequestDto;
import com.example.authentication.dto.user.UserResponseDto;
import com.example.authentication.entities.User;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true))
public interface UserMapper {

    User convertToUser(UserRequestDto userRequestDto);

    UserResponseDto convertToUserResponse(User user);

    List<UserResponseDto> convertToUserResponseDtoList(List<User> users);
}
