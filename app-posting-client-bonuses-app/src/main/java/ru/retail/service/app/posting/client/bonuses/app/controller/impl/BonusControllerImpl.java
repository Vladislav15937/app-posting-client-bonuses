package ru.retail.service.app.posting.client.bonuses.app.controller.impl;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.retail.service.app.posting.client.bonuses.app.controller.BonusController;
import ru.retail.service.app.posting.client.bonuses.app.dto.request.BonusOperationRequest;
import ru.retail.service.app.posting.client.bonuses.app.dto.request.RefundRequest;
import ru.retail.service.app.posting.client.bonuses.app.dto.response.BalanceResponse;
import ru.retail.service.app.posting.client.bonuses.app.dto.response.ErrorResponse;
import ru.retail.service.app.posting.client.bonuses.app.dto.response.TransactionResponse;
import ru.retail.service.app.posting.client.bonuses.app.exception.InvalidOperationException;
import ru.retail.service.app.posting.client.bonuses.app.metrics.MetricService;
import ru.retail.service.app.posting.client.bonuses.app.service.BonusService;
import ru.retail.service.app.posting.client.bonuses.app.utils.limiter.RateLimiterFallbackService;
import ru.retail.service.app.posting.client.bonuses.app.utils.pageblebuilder.PageRequestBuilder;

import java.time.LocalDate;

import static ru.retail.service.app.posting.client.bonuses.app.metrics.name.BonusControllerMetrics.ACCRUE_BONUSES_OPERATION_VALUE;
import static ru.retail.service.app.posting.client.bonuses.app.metrics.name.BonusControllerMetrics.ACCRUE_BONUSES_PROCESSING_TIME;
import static ru.retail.service.app.posting.client.bonuses.app.metrics.name.BonusControllerMetrics.ACCRUE_BONUSES_TOTAL_REQUESTS;
import static ru.retail.service.app.posting.client.bonuses.app.metrics.name.BonusControllerMetrics.GET_BALANCE_BONUSES_OPERATION_VALUE;
import static ru.retail.service.app.posting.client.bonuses.app.metrics.name.BonusControllerMetrics.GET_BALANCE_BONUSES_PROCESSING_TIME;
import static ru.retail.service.app.posting.client.bonuses.app.metrics.name.BonusControllerMetrics.GET_BALANCE_BONUSES_TOTAL_REQUESTS;
import static ru.retail.service.app.posting.client.bonuses.app.metrics.name.BonusControllerMetrics.GET_TRANSACTION_HISTORY_BONUSES_OPERATION_VALUE;
import static ru.retail.service.app.posting.client.bonuses.app.metrics.name.BonusControllerMetrics.GET_TRANSACTION_HISTORY_BONUSES_PROCESSING_TIME;
import static ru.retail.service.app.posting.client.bonuses.app.metrics.name.BonusControllerMetrics.GET_TRANSACTION_HISTORY_BONUSES_TOTAL_REQUESTS;
import static ru.retail.service.app.posting.client.bonuses.app.metrics.name.BonusControllerMetrics.GET_TRANSACTION_HISTORY_BY_PERIOD_BONUSES_OPERATION_VALUE;
import static ru.retail.service.app.posting.client.bonuses.app.metrics.name.BonusControllerMetrics.GET_TRANSACTION_HISTORY_BY_PERIOD_BONUSES_PROCESSING_TIME;
import static ru.retail.service.app.posting.client.bonuses.app.metrics.name.BonusControllerMetrics.GET_TRANSACTION_HISTORY_BY_PERIOD_BONUSES_TOTAL_REQUESTS;
import static ru.retail.service.app.posting.client.bonuses.app.metrics.name.BonusControllerMetrics.REFUND_BONUSES_OPERATION_VALUE;
import static ru.retail.service.app.posting.client.bonuses.app.metrics.name.BonusControllerMetrics.REFUND_BONUSES_PROCESSING_TIME;
import static ru.retail.service.app.posting.client.bonuses.app.metrics.name.BonusControllerMetrics.REFUND_BONUSES_TOTAL_REQUESTS;
import static ru.retail.service.app.posting.client.bonuses.app.metrics.name.BonusControllerMetrics.TAG_OPERATION;
import static ru.retail.service.app.posting.client.bonuses.app.metrics.name.BonusControllerMetrics.TIME_DESC;
import static ru.retail.service.app.posting.client.bonuses.app.metrics.name.BonusControllerMetrics.TOTAL_REQUESTS_DESC;
import static ru.retail.service.app.posting.client.bonuses.app.metrics.name.BonusControllerMetrics.WRITE_OFF_BONUSES_OPERATION_VALUE;
import static ru.retail.service.app.posting.client.bonuses.app.metrics.name.BonusControllerMetrics.WRITE_OFF_BONUSES_PROCESSING_TIME;
import static ru.retail.service.app.posting.client.bonuses.app.metrics.name.BonusControllerMetrics.WRITE_OFF_BONUSES_TOTAL_REQUESTS;

@Slf4j
@RestController
@RequiredArgsConstructor
@Observed
@SuppressWarnings("unused")
public class BonusControllerImpl implements BonusController {

    private final BonusService bonusService;
    private final RateLimiterFallbackService fallbackService;
    private final PageRequestBuilder pageRequestBuilder;
    private final MetricService accrueMetricService;
    private final MetricService getBalanceMetricService;
    private final MetricService getTransactionHistoryByPeriodMetricService;
    private final MetricService getTransactionHistoryMetricService;
    private final MetricService refundBonusesMetricService;
    private final MetricService writeOffBonusesMetricService;

    @Override
    @RateLimiter(name = "bonus-write-api", fallbackMethod = "fallbackAccrue")
    @Timed(value = ACCRUE_BONUSES_PROCESSING_TIME,
            description = TIME_DESC,
            extraTags = {TAG_OPERATION, ACCRUE_BONUSES_OPERATION_VALUE}
    )
    @Counted(
            value = ACCRUE_BONUSES_TOTAL_REQUESTS,
            description = TOTAL_REQUESTS_DESC,
            extraTags = {TAG_OPERATION, ACCRUE_BONUSES_OPERATION_VALUE}
    )
    public ResponseEntity<BalanceResponse> accrueBonuses(BonusOperationRequest request) {
        log.info("POST /api/v1/bonuses/accrue - Начисление бонусов: {}", request.getCardNumber());
        BalanceResponse response = bonusService.accrueBonuses(request);
        accrueMetricService.incrementSuccessRequest();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @RateLimiter(name = "bonus-write-api", fallbackMethod = "fallbackWriteOff")
    @Timed(value = WRITE_OFF_BONUSES_PROCESSING_TIME,
            description = TIME_DESC,
            extraTags = {TAG_OPERATION, WRITE_OFF_BONUSES_OPERATION_VALUE}
    )
    @Counted(
            value = WRITE_OFF_BONUSES_TOTAL_REQUESTS,
            description = TOTAL_REQUESTS_DESC,
            extraTags = {TAG_OPERATION, WRITE_OFF_BONUSES_OPERATION_VALUE}
    )
    public ResponseEntity<BalanceResponse> writeOffBonuses(BonusOperationRequest request) {
        log.info("POST /api/v1/bonuses/write-off - Списание бонусов: {}", request.getCardNumber());
        BalanceResponse response = bonusService.writeOffBonuses(request);
        writeOffBonusesMetricService.incrementSuccessRequest();
        return ResponseEntity.ok(response);
    }

    @Override
    @RateLimiter(name = "bonus-write-api", fallbackMethod = "fallbackRefund")
    @Timed(value = REFUND_BONUSES_PROCESSING_TIME,
            description = TIME_DESC,
            extraTags = {TAG_OPERATION, REFUND_BONUSES_OPERATION_VALUE}
    )
    @Counted(
            value = REFUND_BONUSES_TOTAL_REQUESTS,
            description = TOTAL_REQUESTS_DESC,
            extraTags = {TAG_OPERATION, REFUND_BONUSES_OPERATION_VALUE}
    )
    public ResponseEntity<BalanceResponse> refundBonuses(RefundRequest request) {
        log.info("POST /api/v1/bonuses/refund - Возврат бонусов: {}", request.getCardNumber());
        BalanceResponse response = bonusService.refundBonuses(request);
        refundBonusesMetricService.incrementSuccessRequest();
        return ResponseEntity.ok(response);
    }

    @Override
    @RateLimiter(name = "bonus-read-api", fallbackMethod = "fallbackGetBalance")
    @Timed(value = GET_BALANCE_BONUSES_PROCESSING_TIME,
            description = TIME_DESC,
            extraTags = {TAG_OPERATION, GET_BALANCE_BONUSES_OPERATION_VALUE}
    )
    @Counted(
            value = GET_BALANCE_BONUSES_TOTAL_REQUESTS,
            description = TOTAL_REQUESTS_DESC,
            extraTags = {TAG_OPERATION, GET_BALANCE_BONUSES_OPERATION_VALUE}
    )
    public ResponseEntity<BalanceResponse> getBalance(String cardNumber) {
        log.info("GET /api/v1/bonuses/balance/{} - Запрос баланса", cardNumber);
        BalanceResponse response = bonusService.getBalance(cardNumber);
        getBalanceMetricService.incrementSuccessRequest();
        return ResponseEntity.ok(response);
    }

    @Override
    @RateLimiter(name = "bonus-read-api", fallbackMethod = "fallbackGetHistory")
    @Timed(value = GET_TRANSACTION_HISTORY_BONUSES_PROCESSING_TIME,
            description = TIME_DESC,
            extraTags = {TAG_OPERATION, GET_TRANSACTION_HISTORY_BONUSES_OPERATION_VALUE}
    )
    @Counted(
            value = GET_TRANSACTION_HISTORY_BONUSES_TOTAL_REQUESTS,
            description = TOTAL_REQUESTS_DESC,
            extraTags = {TAG_OPERATION, GET_TRANSACTION_HISTORY_BONUSES_OPERATION_VALUE}
    )
    public ResponseEntity<Page<TransactionResponse>> getTransactionHistory(String cardNumber, int page, int size, String sort) {
        log.info("GET /api/v1/bonuses/history/{} - Запрос истории операций, page={}, size={}, sort={}",
                cardNumber, page, size, sort);
        Pageable pageable = pageRequestBuilder.createPageRequest(page, size, sort);
        Page<TransactionResponse> historyPage = bonusService.getTransactionHistory(cardNumber, pageable);
        getTransactionHistoryMetricService.incrementSuccessRequest();
        return ResponseEntity.ok(historyPage);
    }

    @Override
    @RateLimiter(name = "bonus-read-api", fallbackMethod = "fallbackGetHistoryPeriod")
    @Timed(value = GET_TRANSACTION_HISTORY_BY_PERIOD_BONUSES_PROCESSING_TIME,
            description = TIME_DESC,
            extraTags = {TAG_OPERATION, GET_TRANSACTION_HISTORY_BY_PERIOD_BONUSES_OPERATION_VALUE}
    )
    @Counted(
            value = GET_TRANSACTION_HISTORY_BY_PERIOD_BONUSES_TOTAL_REQUESTS,
            description = TOTAL_REQUESTS_DESC,
            extraTags = {TAG_OPERATION, GET_TRANSACTION_HISTORY_BY_PERIOD_BONUSES_OPERATION_VALUE}
    )
    public ResponseEntity<Page<TransactionResponse>> getTransactionHistoryByPeriod(String cardNumber, LocalDate startDate, LocalDate endDate, int page, int size, String sort) {
        log.info("GET /api/v1/bonuses/history/{}/period - Запрос истории за период с {} по {}, page={}, size={}",
                cardNumber, startDate, endDate, page, size);
        validateDatePeriod(startDate, endDate);
        Pageable pageable = pageRequestBuilder.createPageRequest(page, size, sort);
        Page<TransactionResponse> historyPage = bonusService.getTransactionHistoryByPeriod(
                cardNumber, startDate, endDate, pageable);
        getTransactionHistoryByPeriodMetricService.incrementSuccessRequest();
        return ResponseEntity.ok(historyPage);
    }

    /**
     * Fallback для accrueBonuses (RequestNotPermitted)
     */
    public ResponseEntity<ErrorResponse> fallbackAccrue(
            BonusOperationRequest request,
            io.github.resilience4j.ratelimiter.RequestNotPermitted exception) {
        return fallbackService.buildRateLimitResponse(request, "/api/v1/bonuses/accrue");
    }

    /**
     * Fallback для writeOffBonuses (RequestNotPermitted)
     */
    public ResponseEntity<ErrorResponse> fallbackWriteOff(
            BonusOperationRequest request,
            io.github.resilience4j.ratelimiter.RequestNotPermitted exception) {
        return fallbackService.buildRateLimitResponse(request, "/api/v1/bonuses/write-off");
    }

    /**
     * Fallback для refundBonuses (RequestNotPermitted)
     */
    public ResponseEntity<ErrorResponse> fallbackRefund(
            RefundRequest request,
            io.github.resilience4j.ratelimiter.RequestNotPermitted exception) {
        return fallbackService.buildRateLimitResponse(request, "/api/v1/bonuses/refund");
    }

    /**
     * Fallback для getBalance (RequestNotPermitted)
     */
    public ResponseEntity<ErrorResponse> fallbackGetBalance(
            String cardNumber,
            io.github.resilience4j.ratelimiter.RequestNotPermitted exception) {
        return fallbackService.buildRateLimitResponse(cardNumber, "/api/v1/bonuses/balance");
    }

    /**
     * Fallback для getTransactionHistory (RequestNotPermitted)
     */
    public ResponseEntity<ErrorResponse> fallbackGetHistory(
            String cardNumber,
            int page,
            int size,
            String sort,
            io.github.resilience4j.ratelimiter.RequestNotPermitted exception) {
        return fallbackService.buildRateLimitResponse(cardNumber, "/api/v1/bonuses/history");
    }

    public ResponseEntity<ErrorResponse> fallbackGetHistoryPeriod(
            String cardNumber,
            LocalDate startDate,
            LocalDate endDate,
            int page,
            int size,
            String sort,
            io.github.resilience4j.ratelimiter.RequestNotPermitted exception) {
        return fallbackService.buildRateLimitResponse(cardNumber, "/api/v1/bonuses/history/period");
    }

    private void validateDatePeriod(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new InvalidOperationException("Дата начала не может быть позже даты окончания");
        }
        if (startDate.isAfter(LocalDate.now())) {
            throw new InvalidOperationException("Дата начала не может быть в будущем");
        }
    }
}
