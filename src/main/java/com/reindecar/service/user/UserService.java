package com.reindecar.service.user;

import com.reindecar.common.dto.PageResponse;
import com.reindecar.common.exception.DuplicateEntityException;
import com.reindecar.entity.user.Role;
import com.reindecar.entity.user.User;
import com.reindecar.dto.user.*;
import com.reindecar.exception.user.InvalidCredentialsException;
import com.reindecar.exception.user.UserNotFoundException;
import com.reindecar.exception.user.WeakPasswordException;
import com.reindecar.mapper.user.UserMapper;
import com.reindecar.repository.user.UserRepository;
import com.reindecar.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    private static final Pattern PASSWORD_PATTERN = 
        Pattern.compile("^(?=.*[A-Z])(?=.*\\d).{8,}$");

    @Transactional
    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt for user: {}", request.username());

        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.username().toLowerCase(),
                    request.password()
                )
            );

            User user = userRepository.findByUsername(request.username().toLowerCase())
                .orElseThrow(() -> new UserNotFoundException(request.username()));

            user.updateLastLogin();
            userRepository.save(user);

            String accessToken = jwtTokenProvider.generateAccessToken(
                user.getId(),
                user.getUsername(),
                user.getRole().name(),
                user.getBranchId()
            );

            String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());

            UserResponse userResponse = userMapper.toResponse(user);

            log.info("User logged in successfully: {}", user.getUsername());

            return new LoginResponse(
                accessToken,
                refreshToken,
                jwtTokenProvider.getAccessTokenValidityInSeconds(),
                userResponse
            );

        } catch (AuthenticationException e) {
            log.warn("Failed login attempt for user: {}", request.username());
            throw new InvalidCredentialsException();
        }
    }

    @Transactional
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        log.info("Token refresh attempt");

        if (!jwtTokenProvider.validateToken(request.refreshToken())) {
            throw new InvalidCredentialsException();
        }

        String username = jwtTokenProvider.getUsernameFromToken(request.refreshToken());
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException(username));

        String accessToken = jwtTokenProvider.generateAccessToken(
            user.getId(),
            user.getUsername(),
            user.getRole().name(),
            user.getBranchId()
        );

        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());

        UserResponse userResponse = userMapper.toResponse(user);

        log.info("Token refreshed successfully for user: {}", username);

        return new LoginResponse(
            accessToken,
            refreshToken,
            jwtTokenProvider.getAccessTokenValidityInSeconds(),
            userResponse
        );
    }

    public UserResponse getCurrentUser(String username) {
        log.info("Fetching current user: {}", username);
        User user = userRepository.findByUsername(username.toLowerCase())
            .orElseThrow(() -> new UserNotFoundException(username));
        return userMapper.toResponse(user);
    }

    public PageResponse<UserResponse> getAllUsers(Pageable pageable) {
        log.info("Fetching all users with pagination: {}", pageable);
        Page<User> users = userRepository.findAll(pageable);
        return PageResponse.of(users.map(userMapper::toResponse));
    }

    public UserResponse getUserById(Long id) {
        log.info("Fetching user by id: {}", id);
        User user = findUserByIdOrThrow(id);
        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        log.info("Creating new user with username: {}", request.username());

        validateUniqueUsername(request.username());
        validateUniqueEmail(request.email());
        validatePasswordStrength(request.password());

        if (request.role() == Role.OPERATOR && request.branchId() == null) {
            throw new IllegalArgumentException("OPERATOR users must have a branch assignment");
        }

        String passwordHash = passwordEncoder.encode(request.password());

        User user = User.create(
            request.username(),
            request.email(),
            passwordHash,
            request.firstName(),
            request.lastName(),
            request.role(),
            request.branchId()
        );

        User savedUser = userRepository.save(user);
        log.info("User created successfully with id: {}", savedUser.getId());

        return userMapper.toResponse(savedUser);
    }

    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        log.info("Updating user with id: {}", id);

        User user = findUserByIdOrThrow(id);

        if (!user.getEmail().equals(request.email().toLowerCase())) {
            validateUniqueEmail(request.email());
        }

        user.updateInfo(
            request.email(),
            request.firstName(),
            request.lastName(),
            request.branchId()
        );

        User updatedUser = userRepository.save(user);
        log.info("User updated successfully with id: {}", updatedUser.getId());

        return userMapper.toResponse(updatedUser);
    }

    @Transactional
    public void toggleUserStatus(Long id) {
        log.info("Toggling status for user with id: {}", id);

        User user = findUserByIdOrThrow(id);

        if (user.isActive()) {
            user.deactivate();
            log.info("User deactivated with id: {}", id);
        } else {
            user.activate();
            log.info("User activated with id: {}", id);
        }

        userRepository.save(user);
    }

    @Transactional
    public void changePassword(Long id, ChangePasswordRequest request, String currentUsername) {
        log.info("Changing password for user with id: {}", id);

        User user = findUserByIdOrThrow(id);

        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        validatePasswordStrength(request.newPassword());

        String newPasswordHash = passwordEncoder.encode(request.newPassword());
        user.changePassword(newPasswordHash);

        userRepository.save(user);
        log.info("Password changed successfully for user with id: {}", id);
    }

    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);

        User user = findUserByIdOrThrow(id);
        userRepository.delete(user);

        log.info("User deleted successfully with id: {}", id);
    }

    private User findUserByIdOrThrow(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
    }

    private void validateUniqueUsername(String username) {
        if (userRepository.existsByUsername(username.toLowerCase())) {
            throw new DuplicateEntityException("User", "username", username);
        }
    }

    private void validateUniqueEmail(String email) {
        if (userRepository.existsByEmail(email.toLowerCase())) {
            throw new DuplicateEntityException("User", "email", email);
        }
    }

    private void validatePasswordStrength(String password) {
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new WeakPasswordException();
        }
    }
}
