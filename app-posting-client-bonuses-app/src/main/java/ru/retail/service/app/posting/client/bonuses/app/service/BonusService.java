package ru.retail.service.app.posting.client.bonuses.app.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.retail.service.app.posting.client.bonuses.app.dto.request.BonusOperationRequest;
import ru.retail.service.app.posting.client.bonuses.app.dto.request.RefundRequest;
import ru.retail.service.app.posting.client.bonuses.app.dto.response.BalanceResponse;
import ru.retail.service.app.posting.client.bonuses.app.dto.response.TransactionResponse;

import java.time.LocalDate;

public interface BonusService {

    BalanceResponse accrueBonuses(BonusOperationRequest request);

    BalanceResponse writeOffBonuses(BonusOperationRequest request);

    BalanceResponse refundBonuses(RefundRequest request);

    BalanceResponse getBalance(String cardNumber);

    /**
     * Получить историю операций по карте с пагинацией
     *
     * @param cardNumber номер карты
     * @param pageable   параметры пагинации (page, size, sort)
     * @return страница с историей операций
     */
    Page<TransactionResponse> getTransactionHistory(String cardNumber, Pageable pageable);

    /**
     * Получить историю операций по карте за период с пагинацией
     *
     * @param cardNumber номер карты
     * @param startDate  дата начала (включительно)
     * @param endDate    дата окончания (включительно)
     * @param pageable   параметры пагинации (page, size, sort)
     * @return страница с историей операций за период
     */
    Page<TransactionResponse> getTransactionHistoryByPeriod(
            String cardNumber,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable);
}
