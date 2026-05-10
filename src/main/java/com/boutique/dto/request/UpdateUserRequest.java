package com.boutique.dto.request;

import com.boutique.model.Role;

public record UpdateUserRequest(String name, String email, Role role) {}