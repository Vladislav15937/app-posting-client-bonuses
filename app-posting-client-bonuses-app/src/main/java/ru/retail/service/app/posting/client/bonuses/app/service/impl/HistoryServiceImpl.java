package ru.retail.service.app.posting.client.bonuses.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.retail.service.app.posting.client.bonuses.app.dto.response.TransactionResponse;
import ru.retail.service.app.posting.client.bonuses.app.entity.Transaction;
import ru.retail.service.app.posting.client.bonuses.app.mapper.BonusMapper;
import ru.retail.service.app.posting.client.bonuses.app.repository.TransactionRepository;
import ru.retail.service.app.posting.client.bonuses.app.service.CardValidatorService;
import ru.retail.service.app.posting.client.bonuses.app.service.HistoryService;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class HistoryServiceImpl implements HistoryService {

    private final TransactionRepository transactionRepository;
    private final BonusMapper mapper;
    private final CardValidatorService cardValidator;

    @Override
    public Page<TransactionResponse> getTransactionHistory(String cardNumber, Pageable pageable) {
        log.info("Запрос истории операций: карта={}, page={}, size={}, sort={}",
                cardNumber, pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        cardValidator.validateExists(cardNumber);

        Page<Transaction> transactionsPage = transactionRepository
                .findByCard_CardNumber(cardNumber, pageable);

        return transactionsPage.map(mapper::toTransactionResponse);
    }

    @Override
    public Page<TransactionResponse> getTransactionHistoryByPeriod(
            String cardNumber,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable) {

        log.info("Запрос истории операций за период: карта={}, с={}, по={}, page={}, size={}",
                cardNumber, startDate, endDate, pageable.getPageNumber(), pageable.getPageSize());

        cardValidator.validateExists(cardNumber);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        Page<Transaction> transactionsPage = transactionRepository
                .findByCard_CardNumberAndCreatedAtBetween(cardNumber, startDateTime, endDateTime, pageable);

        return transactionsPage.map(mapper::toTransactionResponse);
    }
}
