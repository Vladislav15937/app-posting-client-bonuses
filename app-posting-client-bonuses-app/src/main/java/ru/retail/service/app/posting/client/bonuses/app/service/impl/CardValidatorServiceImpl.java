package ru.retail.service.app.posting.client.bonuses.app.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.retail.service.app.posting.client.bonuses.app.dto.request.RefundRequest;
import ru.retail.service.app.posting.client.bonuses.app.entity.Card;
import ru.retail.service.app.posting.client.bonuses.app.entity.Transaction;
import ru.retail.service.app.posting.client.bonuses.app.exception.CardNotFoundException;
import ru.retail.service.app.posting.client.bonuses.app.exception.InsufficientBalanceException;
import ru.retail.service.app.posting.client.bonuses.app.exception.InvalidOperationException;
import ru.retail.service.app.posting.client.bonuses.app.repository.CardRepository;
import ru.retail.service.app.posting.client.bonuses.app.service.CardValidatorService;

@Service
@RequiredArgsConstructor
public class CardValidatorServiceImpl implements CardValidatorService {

    private final CardRepository cardRepository;

    @Override
    public void validateActive(Card card) {
        if (!"ACTIVE".equals(card.getStatus())) {
            throw new InvalidOperationException("Карта неактивна. Статус: " + card.getStatus());
        }
    }

    @Override
    public void validateExists(String cardNumber) {
        cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new CardNotFoundException(cardNumber));
    }

    @Override
    public void validateSufficientBalance(Card card, Long requestedAmount) {
        if (card.getBalance() < requestedAmount) {
            throw new InsufficientBalanceException(
                    card.getCardNumber(),
                    requestedAmount,
                    card.getBalance()
            );
        }
    }

    @Override
    public void validatePositiveBalance(long newBalance, RefundRequest request, Transaction originalTransaction) {
        if (newBalance < 0) {
            throw new InsufficientBalanceException(
                    request.getCardNumber(),
                    Math.abs(originalTransaction.getAmount()),
                    originalTransaction.getCard().getBalance()
            );
        }
    }
}
