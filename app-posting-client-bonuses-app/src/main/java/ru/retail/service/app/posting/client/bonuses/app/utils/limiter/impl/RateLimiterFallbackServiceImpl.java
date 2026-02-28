package ru.retail.service.app.posting.client.bonuses.app.utils.limiter.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.retail.service.app.posting.client.bonuses.app.dto.request.BonusOperationRequest;
import ru.retail.service.app.posting.client.bonuses.app.dto.request.RefundRequest;
import ru.retail.service.app.posting.client.bonuses.app.dto.response.ErrorResponse;
import ru.retail.service.app.posting.client.bonuses.app.utils.limiter.RateLimiterFallbackService;

import java.time.LocalDateTime;

@Slf4j
@Service
public class RateLimiterFallbackServiceImpl implements RateLimiterFallbackService {

    /**
     * Построить ответ при превышении rate limit для любого объекта
     */
    @Override
    public ResponseEntity<ErrorResponse> buildRateLimitResponse(Object request, String path) {
        log.warn("Rate limit exceeded for request: {}, path: {}",
                request != null ? request.getClass().getSimpleName() : "unknown",
                path);

        return buildErrorResponse(
                path
        );
    }

    /**
     * Построить ответ при превышении rate limit для BonusOperationRequest
     */
    @Override
    public ResponseEntity<ErrorResponse> buildRateLimitResponse(BonusOperationRequest request, String path) {
        log.warn("Rate limit exceeded for BonusOperationRequest, card: {}, path: {}",
                request != null ? request.getCardNumber() : "unknown",
                path);

        return buildErrorResponse(
                path
        );
    }

    /**
     * Построить ответ при превышении rate limit для RefundRequest
     */
    @Override
    public ResponseEntity<ErrorResponse> buildRateLimitResponse(RefundRequest request, String path) {
        log.warn("Rate limit exceeded for RefundRequest, card: {}, path: {}",
                request != null ? request.getCardNumber() : "unknown",
                path);

        return buildErrorResponse(
                path
        );
    }

    /**
     * Построить ответ при превышении rate limit для cardNumber
     */
    @Override
    public ResponseEntity<ErrorResponse> buildRateLimitResponse(String cardNumber, String path) {
        log.warn("Rate limit exceeded for card: {}, path: {}", cardNumber, path);
        return buildRateLimitResponse((Object) cardNumber, path);
    }

    /**
     * Вспомогательный метод для создания ответа с ошибкой
     */
    private ResponseEntity<ErrorResponse> buildErrorResponse(String path) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.TOO_MANY_REQUESTS.value(),
                "Слишком много запросов. Пожалуйста, повторите попытку позже.",
                LocalDateTime.now(),
                path
        );
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(error);
    }
}
