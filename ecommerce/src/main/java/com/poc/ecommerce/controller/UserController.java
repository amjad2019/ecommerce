package com.poc.ecommerce.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.poc.ecommerce.domain.user.AuthService;
import com.poc.ecommerce.domain.user.UserService;
import com.poc.ecommerce.dto.auth.UserResponse;
import com.poc.ecommerce.dto.auth.UserUpdateRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final AuthService authService;
    
    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getCurrentUserProfile() {
        var user = authService.getCurrentUser();
        UserResponse response = new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getRole()
        );
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateCurrentUserProfile(
            @Valid @RequestBody UserUpdateRequest request) {
        var user = authService.getCurrentUser();
        UserResponse response = userService.updateUser(user.getId(), request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/upgrade-to-premium")
    public ResponseEntity<UserResponse> upgradeToPremium() {
        var user = authService.getCurrentUser();
        UserResponse response = userService.upgradeToPremium(user.getId());
        return ResponseEntity.ok(response);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/all")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/{id}/make-admin")
    public ResponseEntity<UserResponse> makeAdmin(@PathVariable Long id) {
        UserResponse response = userService.assignAdminRole(id);
        return ResponseEntity.ok(response);
    }
}