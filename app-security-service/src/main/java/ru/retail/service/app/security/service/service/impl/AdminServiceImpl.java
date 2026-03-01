package ru.retail.service.app.security.service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.retail.service.app.security.service.dto.request.AssignRolesRequest;
import ru.retail.service.app.security.service.dto.request.CreateUserRequest;
import ru.retail.service.app.security.service.dto.response.UserResponse;
import ru.retail.service.app.security.service.repository.RoleRepository;
import ru.retail.service.app.security.service.repository.UserRepository;
import ru.retail.service.app.security.service.service.AdminService;
import ru.retail.service.app.security.service.entity.Role;
import ru.retail.service.app.security.service.entity.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
        return ResponseEntity.ok(users);
    }

    @Override
    public ResponseEntity<UserResponse> getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        return ResponseEntity.ok(mapToResponse(user));
    }

    @Override
    public ResponseEntity<UserResponse> createUser(CreateUserRequest request) {
        log.info("Creating new user: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists: " + request.getUsername());
        }
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setClientId(request.getClientId());

        Set<Role> roles = new HashSet<>();
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            roles = request.getRoles().stream()
                    .map(roleName -> {
                        Role role = new Role();
                        role.setId(getRoleIdByName(roleName));
                        role.setName(roleName);
                        return role;
                    })
                    .collect(Collectors.toSet());
        }
        user.setRoles(roles);

        User savedUser = userRepository.save(user);
        log.info("User created successfully: {}", request.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(savedUser));
    }

    @Override
    public ResponseEntity<UserResponse> assignRoles(String username, AssignRolesRequest request) {
        log.info("Assigning roles to user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Set<Role> roles = request.getRoles().stream()
                .map(roleName -> {
                    Role role = new Role();
                    role.setId(getRoleIdByName(roleName));
                    role.setName(roleName);
                    return role;
                })
                .collect(Collectors.toSet());

        user.setRoles(roles);
        User updatedUser = userRepository.save(user);

        log.info("Roles assigned to user {}: {}", username, request.getRoles());

        return ResponseEntity.ok(mapToResponse(updatedUser));
    }

    @Override
    public void deleteUser(String username) {
        log.info("Deleting user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        userRepository.delete(user);
        log.info("User deleted: {}", username);
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .clientId(user.getClientId())
                .roles(user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()))
                .createdAt(user.getCreatedAt())
                .build();
    }

    private Long getRoleIdByName(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName))
                .getId();
    }
}
