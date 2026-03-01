package ru.retail.service.app.security.service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.retail.service.app.security.service.config.jwt.JwtService;
import ru.retail.service.app.security.service.dto.request.LoginRequest;
import ru.retail.service.app.security.service.dto.response.JwtResponse;
import ru.retail.service.app.security.service.repository.UserRepository;
import ru.retail.service.app.security.service.service.AuthService;
import ru.retail.service.app.security.service.entity.Role;
import ru.retail.service.app.security.service.entity.User;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<?> authorise(LoginRequest request) {
        var userOpt = userRepository.findByUsername(request.getUsername());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            boolean matches = passwordEncoder.matches(request.getPassword(), user.getPassword());
            log.info("Password matches: {}", matches);
            log.info("User roles: {}", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
        } else {
            log.info("User NOT found in DB: {}", request.getUsername());
        }
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(userDetails);
            log.info("User {} successfully logged in", request.getUsername());
            return ResponseEntity.ok(JwtResponse.builder()
                    .token(token)
                    .username(userDetails.getUsername())
                    .roles(userDetails.getAuthorities().stream()
                            .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                            .toList())
                    .build());
        } catch (BadCredentialsException e) {
            log.warn("Bad credentials for user: {}", request.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Неверные учетные данные"));
        } catch (Exception e) {
            log.error("Login error for user: {}", request.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Внутренняя ошибка сервера"));
        }
    }
}
