package com.example.authentication.services.impl;

import com.example.authentication.dto.user.UserChangePasswordDto;
import com.example.authentication.dto.user.UserForgotPasswordDto;
import com.example.authentication.dto.user.UserRequestDto;
import com.example.authentication.dto.user.UserResetPasswordDto;
import com.example.authentication.dto.user.UserResponseDto;
import com.example.authentication.entities.User;
import com.example.authentication.entities.VerificationToken;
import com.example.authentication.entities.enums.Role;
import com.example.authentication.entities.enums.Status;
import com.example.authentication.mapper.UserMapper;
import com.example.authentication.repositories.UserRepository;
import com.example.authentication.services.EmailService;
import com.example.authentication.services.UserService;
import com.example.authentication.services.VerificationTokenService;
import com.example.authentication.services.exception.UpdatePasswordException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final String USER_NOT_FOUND_EXCEPTION = "Specified user is not found";
    private static final String USER_ALREADY_EXISTS_EXCEPTION = "There is an account with that email address: ";
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final VerificationTokenService verificationTokenService;

    @Override
    @Transactional
    public UserResponseDto registerUser(UserRequestDto userRequestDto) {
        checkEmailDuplicate(userRequestDto);
        User user = populateUserData(userRequestDto);
        user = userRepository.save(user);
        VerificationToken verificationToken = verificationTokenService.saveToken(user);
        emailService.sendRegistrationConfirmationEmail(user.getEmail(), verificationToken.getToken());
        return userMapper.convertToUserResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return getUserByMailIfExist(email);
    }

    @Override
    @Transactional
    public void verifyUserAccount(String token) {
        verificationTokenService.validateVerificationToken(token);
        User user = findUserByVerificationToken(token);
        user.setStatus(Status.ACTIVE);
        userRepository.save(user);
        verificationTokenService.removeToken(token);
    }

    @Override
    @Transactional
    public void forgotPassword(UserForgotPasswordDto userForgotPasswordDto) {
        String email = userForgotPasswordDto.getEmail();
        User user = getUserByMailIfExist(email);
        VerificationToken verificationToken = verificationTokenService.saveToken(user);
        emailService.sendRegistrationConfirmationEmail(user.getEmail(), verificationToken.getToken());
    }

    @Override
    @Transactional
    public void resetPasswordByEmail(UserResetPasswordDto userDto, String token) {
        verificationTokenService.validateResetPasswordToken(token);
        User user = findUserByVerificationToken(token);
        if (!user.getPassword().equals(userDto.getMatchingPassword())) {
            throw new UpdatePasswordException("");
        }
        user.setPassword(passwordEncoder.encode(userDto.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updatePassword(UserChangePasswordDto userChangePasswordDto) {
        User currentUser = getCurrentUser();
        if (!currentPasswordMatches(currentUser.getPassword(), userChangePasswordDto.getCurrentPassword())) {
            throw new UpdatePasswordException("");
        }
        currentUser.setPassword(userChangePasswordDto.getNewPassword());
        userRepository.save(currentUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto findUserById(Long id) {
        User user = getUserByIdIfExist(id);
        return userMapper.convertToUserResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByMailIfExist(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new EntityNotFoundException(USER_NOT_FOUND_EXCEPTION));
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserByVerificationToken(String token) {
        return userRepository.findUserByToken(token).orElseThrow(() ->
                new EntityNotFoundException(USER_NOT_FOUND_EXCEPTION));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> findAll(Pageable pageable) {
        List<User> users = userRepository.findAll(pageable).toList();
        return userMapper.convertToUserResponseDtoList(users);
    }

    private User populateUserData(UserRequestDto userRequestDto) {
        return User.builder()
                .withEmail(userRequestDto.getEmail())
                .withPassword(passwordEncoder.encode(userRequestDto.getPassword()))
                .withRole(Role.USER)
                .withStatus(Status.WAITING_ACTIVATION)
                .build();
    }

    private User getUserByIdIfExist(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(USER_NOT_FOUND_EXCEPTION));
    }

    private void checkEmailDuplicate(UserRequestDto userRequestDto) {
        String email = userRequestDto.getEmail();
        if (isUserExistByEmail(email)) {
            throw new EntityExistsException(USER_ALREADY_EXISTS_EXCEPTION + email);
        }
    }

    private boolean isUserExistByEmail(String email) {
       return userRepository.findByEmail(email).isPresent();
    }

    private Boolean currentPasswordMatches(String currentPassword, String newPassword) {
        return passwordEncoder.matches(currentPassword, newPassword);
    }
}
