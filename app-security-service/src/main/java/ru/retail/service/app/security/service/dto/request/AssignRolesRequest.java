package ru.retail.service.app.security.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

@Data
public class AssignRolesRequest {

    @NotBlank(message = "Имя пользователя обязательно")
    private String username;

    @NotNull(message = "Список ролей обязателен")
    private Set<String> roles;
}
