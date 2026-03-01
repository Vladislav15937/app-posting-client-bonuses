package ru.retail.service.app.security.service.service;

import org.springframework.http.ResponseEntity;
import ru.retail.service.app.security.service.dto.request.AssignRolesRequest;
import ru.retail.service.app.security.service.dto.request.CreateUserRequest;
import ru.retail.service.app.security.service.dto.response.UserResponse;

import java.util.List;

public interface AdminService {

    ResponseEntity<List<UserResponse>> getAllUsers();

    ResponseEntity<UserResponse> getUserByUsername(String username);

    ResponseEntity<UserResponse> createUser(CreateUserRequest request);

    ResponseEntity<UserResponse> assignRoles(String username, AssignRolesRequest request);

    void deleteUser(String username);
}
