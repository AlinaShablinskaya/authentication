package com.example.user.services.impl;

import com.example.user.dto.user.UserRequestDto;
import com.example.user.dto.user.UserResponseDto;
import com.example.user.entities.User;
import com.example.user.entities.enums.Role;
import com.example.user.mapper.UserMapper;
import com.example.user.repositories.UserRepository;
import com.example.user.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final String USER_NOT_FOUND_EXCEPTION = "Specified user is not found";
    private static final String USER_ALREADY_EXISTS_EXCEPTION = "Specified user already exists";
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponseDto registerUser(UserRequestDto userRequestDto) {
        checkEmailDuplicate(userRequestDto);
        User user = populateUserData(userRequestDto);
        user = userRepository.save(user);
        return userMapper.convertToUserResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto findUserById(Long id) {
        User user = getUserByIdIfExist(id);
        return userMapper.convertToUserResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> findAll(Pageable pageable) {
        List<User> users = userRepository.findAll(pageable).toList();
        return userMapper.convertToUserResponseDtoList(users);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (isUserNotFound(id)) {
            throw new EntityNotFoundException(USER_NOT_FOUND_EXCEPTION);
        }
        userRepository.softDelete(id);
    }

    private User populateUserData(UserRequestDto userRequestDto) {
        User user = new User();
        user.setEmail(userRequestDto.getEmail());
        user.setPassword(encodePassword(userRequestDto));
        user.setRole(Role.USER);
        return user;
    }

    private User getUserByIdIfExist(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(USER_NOT_FOUND_EXCEPTION));
    }

    private String encodePassword(UserRequestDto userRequestDto) {
        String password = userRequestDto.getPassword();
        return passwordEncoder.encode(password);
    }

    private void checkEmailDuplicate(UserRequestDto userRequestDto) {
        String email = userRequestDto.getEmail();
        if (isUserExistByEmail(email)) {
            throw new EntityExistsException(USER_ALREADY_EXISTS_EXCEPTION);
        }
    }

    private boolean isUserExistByEmail(String email) {
       return userRepository.findByEmail(email).isPresent();
    }

    private boolean isUserNotFound(Long id) {
        return userRepository.findById(id).isEmpty();
    }
}
