package com.supermarket.service;

import com.supermarket.dto.request.UpdateUserRequest;
import com.supermarket.dto.request.UserRequest;
import com.supermarket.dto.response.UserResponse;
import com.supermarket.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponse createUser(UserRequest request);
    UserResponse updateUser(Long id, UpdateUserRequest request);
    void deleteUser(Long id);
    UserResponse getUser(Long id);
    Page<UserResponse> getAllUsers(UserRole role, Boolean active, String search, Pageable pageable);
    UserResponse toggleAccountStatus(Long id);
    UserResponse getUserByEmail(String email);
}
