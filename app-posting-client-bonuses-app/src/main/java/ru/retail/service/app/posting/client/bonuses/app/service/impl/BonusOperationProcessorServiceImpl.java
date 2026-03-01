package ru.retail.service.app.posting.client.bonuses.app.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.retail.service.app.posting.client.bonuses.app.dto.request.BonusOperationRequest;
import ru.retail.service.app.posting.client.bonuses.app.dto.response.BalanceResponse;
import ru.retail.service.app.posting.client.bonuses.app.entity.Card;
import ru.retail.service.app.posting.client.bonuses.app.entity.Transaction;
import ru.retail.service.app.posting.client.bonuses.app.exception.CardNotFoundException;
import ru.retail.service.app.posting.client.bonuses.app.mapper.BonusMapper;
import ru.retail.service.app.posting.client.bonuses.app.repository.CardRepository;
import ru.retail.service.app.posting.client.bonuses.app.service.BalanceCalculatorService;
import ru.retail.service.app.posting.client.bonuses.app.service.BonusOperationProcessorService;
import ru.retail.service.app.posting.client.bonuses.app.service.CardValidatorService;
import ru.retail.service.app.posting.client.bonuses.app.service.IdempotencyService;
import ru.retail.service.app.posting.client.bonuses.app.service.TransactionFactoryService;
import ru.retail.service.app.posting.client.bonuses.app.service.utils.BalanceCalculationResult;
import ru.retail.service.app.posting.client.bonuses.app.service.utils.OperationType;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BonusOperationProcessorServiceImpl implements BonusOperationProcessorService {

    private final CardRepository cardRepository;
    private final BonusMapper mapper;
    private final IdempotencyService idempotencyService;
    private final CardValidatorService cardValidator;
    private final BalanceCalculatorService balanceCalculator;
    private final TransactionFactoryService transactionFactory;

    @Override
    public BalanceResponse process(BonusOperationRequest request, OperationType operationType, boolean allowNewCard) {
        log.info("{} бонусов: карта={}, сумма={}",
                operationType.getLogPrefix(), request.getCardNumber(), request.getAmount());
        idempotencyService.checkIdempotency(request.getIdempotencyKey());
        Card card = getCard(request.getCardNumber(), allowNewCard);
        cardValidator.validateActive(card);
        if (operationType == OperationType.WRITE_OFF) {
            cardValidator.validateSufficientBalance(card, request.getAmount());
        }
        BalanceCalculationResult balanceResult = balanceCalculator.calculate(
                card.getBalance(),
                request.getAmount(),
                operationType
        );
        Transaction transaction = transactionFactory.createOperationTransaction(
                card,
                operationType.getSignedAmount(request.getAmount()),
                operationType.getTransactionType(),
                balanceResult,
                request
        );
        card.addTransaction(transaction);
        card.setBalance(balanceResult.getNewBalance());
        cardRepository.save(card);
        log.info("Бонусы {}: карта={}, сумма={}, новый баланс={}",
                operationType.getPastTense(),
                request.getCardNumber(),
                request.getAmount(),
                balanceResult.getNewBalance());
        return mapper.toBalanceResponse(card);
    }

    private Card getCard(String cardNumber, boolean allowNewCard) {
        if (allowNewCard) {
            return cardRepository.findByCardNumberWithLock(cardNumber)
                    .orElseGet(() -> createNewCard(cardNumber));
        }
        return cardRepository.findByCardNumberWithLock(cardNumber)
                .orElseThrow(() -> new CardNotFoundException(cardNumber));
    }

    private Card createNewCard(String cardNumber) {
        log.info("Создание новой карты: {}", cardNumber);
        Card card = new Card();
        card.setCardNumber(cardNumber);
        card.setBalance(0L);
        card.setStatus("ACTIVE");
        card.setCreatedBy("SYSTEM");
        return cardRepository.save(card);
    }
}
