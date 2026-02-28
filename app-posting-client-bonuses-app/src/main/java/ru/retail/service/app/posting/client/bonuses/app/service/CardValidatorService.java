package ru.retail.service.app.posting.client.bonuses.app.service;

import ru.retail.service.app.posting.client.bonuses.app.dto.request.RefundRequest;
import ru.retail.service.app.posting.client.bonuses.app.entity.Card;
import ru.retail.service.app.posting.client.bonuses.app.entity.Transaction;

public interface CardValidatorService {

    void validateActive(Card card);

    void validateSufficientBalance(Card card, Long amount);

    void validatePositiveBalance(long newBalance, RefundRequest request, Transaction originalTransaction);

    void validateExists(String cardNumber);
}
