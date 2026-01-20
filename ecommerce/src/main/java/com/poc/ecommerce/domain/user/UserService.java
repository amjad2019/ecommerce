package com.poc.ecommerce.domain.user;
import com.poc.ecommerce.domain.user.Role;
import com.poc.ecommerce.domain.user.User;
import com.poc.ecommerce.dto.auth.UserResponse;
import com.poc.ecommerce.dto.auth.UserUpdateRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return convertToResponse(user);
    }
    
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        // Update username if provided and unique
        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new RuntimeException("Username is already taken");
            }
            user.setUsername(request.getUsername());
        }
        
        // Update password if provided
        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        // Update role if provided (only ADMIN can change roles)
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        
        user = userRepository.save(user);
        return convertToResponse(user);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
    
    @Transactional
    public UserResponse upgradeToPremium(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        if (user.getRole() == Role.PREMIUM_USER) {
            throw new RuntimeException("User is already a premium user");
        }
        
        user.setRole(Role.PREMIUM_USER);
        user = userRepository.save(user);
        
        return convertToResponse(user);
    }
    
    @Transactional
    public UserResponse assignAdminRole(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        if (user.getRole() == Role.ADMIN) {
            throw new RuntimeException("User is already an admin");
        }
        
        user.setRole(Role.ADMIN);
        user = userRepository.save(user);
        
        return convertToResponse(user);
    }
    
    private UserResponse convertToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getRole()
        );
    }
}