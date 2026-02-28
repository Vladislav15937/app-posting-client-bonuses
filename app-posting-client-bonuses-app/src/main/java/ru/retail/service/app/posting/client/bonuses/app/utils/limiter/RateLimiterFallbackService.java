package ru.retail.service.app.posting.client.bonuses.app.utils.limiter;

import org.springframework.http.ResponseEntity;
import ru.retail.service.app.posting.client.bonuses.app.dto.request.BonusOperationRequest;
import ru.retail.service.app.posting.client.bonuses.app.dto.request.RefundRequest;
import ru.retail.service.app.posting.client.bonuses.app.dto.response.ErrorResponse;

public interface RateLimiterFallbackService {

    ResponseEntity<ErrorResponse> buildRateLimitResponse(Object request, String path);

    ResponseEntity<ErrorResponse> buildRateLimitResponse(BonusOperationRequest request, String path);

    ResponseEntity<ErrorResponse> buildRateLimitResponse(RefundRequest request, String path);

    ResponseEntity<ErrorResponse> buildRateLimitResponse(String cardNumber, String path);
}
