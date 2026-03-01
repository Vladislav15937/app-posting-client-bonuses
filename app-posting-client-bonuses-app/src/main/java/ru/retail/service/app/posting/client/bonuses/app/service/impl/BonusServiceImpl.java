package ru.retail.service.app.posting.client.bonuses.app.service.impl;

import io.micrometer.observation.annotation.Observed;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.retail.service.app.posting.client.bonuses.app.dto.request.BonusOperationRequest;
import ru.retail.service.app.posting.client.bonuses.app.dto.request.RefundRequest;
import ru.retail.service.app.posting.client.bonuses.app.dto.response.BalanceResponse;
import ru.retail.service.app.posting.client.bonuses.app.dto.response.TransactionResponse;
import ru.retail.service.app.posting.client.bonuses.app.service.BalanceService;
import ru.retail.service.app.posting.client.bonuses.app.service.BonusOperationProcessorService;
import ru.retail.service.app.posting.client.bonuses.app.service.BonusService;
import ru.retail.service.app.posting.client.bonuses.app.service.HistoryService;
import ru.retail.service.app.posting.client.bonuses.app.service.RefundProcessorService;
import ru.retail.service.app.posting.client.bonuses.app.service.utils.OperationType;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
@Observed
public class BonusServiceImpl implements BonusService {

    private final BonusOperationProcessorService operationProcessor;
    private final RefundProcessorService refundProcessor;
    private final BalanceService balanceService;
    private final HistoryService historyService;

    @Override
    @Retryable(
            value = {OptimisticLockException.class, CannotAcquireLockException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 50, multiplier = 2)
    )
    @CacheEvict(value = "balances", key = "#request.cardNumber")
    public BalanceResponse accrueBonuses(BonusOperationRequest request) {
        return operationProcessor.process(
                request,
                OperationType.ACCRUE,
                true
        );
    }

    @Override
    @Retryable(
            value = {OptimisticLockException.class, CannotAcquireLockException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 50, multiplier = 2)
    )
    @CacheEvict(value = "balances", key = "#request.cardNumber")
    public BalanceResponse writeOffBonuses(BonusOperationRequest request) {
        return operationProcessor.process(
                request,
                OperationType.WRITE_OFF,
                false
        );
    }

    @Override
    @Retryable(
            value = {OptimisticLockException.class, CannotAcquireLockException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 50, multiplier = 2)
    )
    @CacheEvict(value = "balances", key = "#request.cardNumber")
    public BalanceResponse refundBonuses(RefundRequest request) {
        return refundProcessor.process(request);
    }

    @Override
    @Cacheable(value = "balances", key = "#cardNumber")
    @Transactional(readOnly = true)
    public BalanceResponse getBalance(String cardNumber) {
        return balanceService.getBalance(cardNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionResponse> getTransactionHistory(String cardNumber, Pageable pageable) {
        return historyService.getTransactionHistory(cardNumber, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionResponse> getTransactionHistoryByPeriod(
            String cardNumber,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable) {
        return historyService.getTransactionHistoryByPeriod(cardNumber, startDate, endDate, pageable);
    }
}