package ru.retail.service.app.security.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class CreateUserRequest {

    @NotBlank(message = "Имя пользователя обязательно")
    @Size(min = 1, max = 50, message = "Имя пользователя должно быть от 1 до 50 символов")
    private String username;

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 1, message = "Пароль должен быть не менее 1 символов")
    private String password;

    private String email;

    private String clientId;

    private Set<String> roles;
}
