package ru.retail.service.app.posting.client.bonuses.app.mapper;

import org.springframework.stereotype.Component;
import ru.retail.service.app.posting.client.bonuses.app.dto.response.BalanceResponse;
import ru.retail.service.app.posting.client.bonuses.app.dto.response.TransactionResponse;
import ru.retail.service.app.posting.client.bonuses.app.entity.Card;
import ru.retail.service.app.posting.client.bonuses.app.entity.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class BonusMapper {

    public BalanceResponse toBalanceResponse(Card card) {
        return BalanceResponse.builder()
                .cardNumber(card.getCardNumber())
                .balance(BigDecimal.valueOf(card.getBalance(), 2))
                .balanceInKopecks(card.getBalance())
                .status(card.getStatus())
                .timestamp(LocalDateTime.now())
                .build();
    }

    public TransactionResponse toTransactionResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .transactionUuid(transaction.getTransactionUuid())
                .cardNumber(transaction.getCard().getCardNumber())
                .amount(BigDecimal.valueOf(transaction.getAmount(), 2))
                .amountInKopecks(transaction.getAmount())
                .type(transaction.getType().toString())
                .balanceBefore(BigDecimal.valueOf(transaction.getBalanceBefore(), 2))
                .balanceAfter(BigDecimal.valueOf(transaction.getBalanceAfter(), 2))
                .description(transaction.getDescription())
                .status(transaction.getStatus().toString())
                .createdAt(transaction.getCreatedAt())
                .parentTransactionId(transaction.getParentTransaction() != null ?
                        transaction.getParentTransaction().getId() : null)
                .build();
    }
}