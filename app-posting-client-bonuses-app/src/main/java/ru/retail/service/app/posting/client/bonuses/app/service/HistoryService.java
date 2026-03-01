package ru.retail.service.app.posting.client.bonuses.app.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.retail.service.app.posting.client.bonuses.app.dto.response.TransactionResponse;

import java.time.LocalDate;

public interface HistoryService {

    Page<TransactionResponse> getTransactionHistory(String cardNumber, Pageable pageable);

    Page<TransactionResponse> getTransactionHistoryByPeriod(String cardNumber, LocalDate startDate, LocalDate endDate, Pageable pageable);
}
