package com.poc.ecommerce.dto.auth;

import com.poc.ecommerce.domain.user.Role;

public record RegisterRequest(String username, String email, String password, String firstName, String lastName, Role role) {}