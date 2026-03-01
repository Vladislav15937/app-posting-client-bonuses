package ru.retail.service.app.security.service.config.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    /**
     * Секретный ключ для подписи JWT токенов
     */
    private String secret = "mySuperSecretKeyForJWTGeneration20260301WithEnoughLength12345";

    /**
     * Время жизни токена в миллисекундах (по умолчанию 24 часа)
     */
    private long expiration = 86400000;

    /**
     * Заголовок для передачи токена
     */
    private String header = "Authorization";

    /**
     * Префикс токена
     */
    private String prefix = "Bearer ";
}