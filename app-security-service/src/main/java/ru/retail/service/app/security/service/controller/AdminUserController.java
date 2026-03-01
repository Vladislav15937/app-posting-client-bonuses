package ru.retail.service.app.security.service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.retail.service.app.security.service.dto.request.AssignRolesRequest;
import ru.retail.service.app.security.service.dto.request.CreateUserRequest;
import ru.retail.service.app.security.service.dto.response.UserResponse;
import ru.retail.service.app.security.service.service.AdminService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/admin/users")
@RequiredArgsConstructor
@Tag(name = "Управлением доступами персонала", description = "API для выдачи и редактирования прав доступа")
public class AdminUserController {

    private final AdminService adminService;

    @GetMapping
    @Operation(summary = "Получение списка пользователей")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return adminService.getAllUsers();
    }

    @GetMapping("/{username}")
    @Operation(summary = "Получение пользователя по имени")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        return adminService.getUserByUsername(username);
    }

    @PostMapping
    @Operation(summary = "Создать пользователя")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        return adminService.createUser(request);
    }

    @PutMapping("/{username}/roles")
    @Operation(summary = "Назначение ролей")
    public ResponseEntity<UserResponse> assignRoles(
            @PathVariable String username,
            @Valid @RequestBody AssignRolesRequest request) {

        return adminService.assignRoles(username, request);
    }

    @DeleteMapping("/{username}")
    @Operation(summary = "Удаление пользователя")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String username) {
        adminService.deleteUser(username);
    }
}
