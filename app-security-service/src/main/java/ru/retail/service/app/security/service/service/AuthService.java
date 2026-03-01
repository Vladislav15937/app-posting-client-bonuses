package ru.retail.service.app.security.service.service;

import org.springframework.http.ResponseEntity;
import ru.retail.service.app.security.service.dto.request.LoginRequest;

public interface AuthService {

    ResponseEntity<?> authorise(LoginRequest request);
}
