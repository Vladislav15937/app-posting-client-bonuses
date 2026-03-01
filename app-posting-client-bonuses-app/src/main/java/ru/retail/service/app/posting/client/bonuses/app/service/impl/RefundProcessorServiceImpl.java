package ru.retail.service.app.posting.client.bonuses.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.retail.service.app.posting.client.bonuses.app.dto.request.RefundRequest;
import ru.retail.service.app.posting.client.bonuses.app.dto.response.BalanceResponse;
import ru.retail.service.app.posting.client.bonuses.app.entity.Card;
import ru.retail.service.app.posting.client.bonuses.app.entity.Transaction;
import ru.retail.service.app.posting.client.bonuses.app.exception.CardNotFoundException;
import ru.retail.service.app.posting.client.bonuses.app.exception.InvalidOperationException;
import ru.retail.service.app.posting.client.bonuses.app.mapper.BonusMapper;
import ru.retail.service.app.posting.client.bonuses.app.repository.CardRepository;
import ru.retail.service.app.posting.client.bonuses.app.repository.TransactionRepository;
import ru.retail.service.app.posting.client.bonuses.app.service.CardValidatorService;
import ru.retail.service.app.posting.client.bonuses.app.service.IdempotencyService;
import ru.retail.service.app.posting.client.bonuses.app.service.RefundCalculatorService;
import ru.retail.service.app.posting.client.bonuses.app.service.RefundProcessorService;
import ru.retail.service.app.posting.client.bonuses.app.service.TransactionFactoryService;
import ru.retail.service.app.posting.client.bonuses.app.service.utils.RefundCalculationResult;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RefundProcessorServiceImpl implements RefundProcessorService {

    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;
    private final BonusMapper mapper;
    private final IdempotencyService idempotencyService;
    private final RefundCalculatorService refundCalculator;
    private final TransactionFactoryService transactionFactory;
    private final CardValidatorService cardValidator;

    @Override
    public BalanceResponse process(RefundRequest request) {
        log.info("Возврат бонусов: карта={}, оригинальная транзакция={}",
                request.getCardNumber(), request.getOriginalTransactionId());
        idempotencyService.checkIdempotency(String.valueOf(request.getIdempotencyKey()));
        Card card = getCardWithLock(request.getCardNumber());
        Transaction originalTransaction = validateOriginalTransaction(request, card);
        RefundCalculationResult refundResult = refundCalculator.calculate(
                card.getBalance(),
                originalTransaction
        );
        cardValidator.validatePositiveBalance(refundResult.getNewBalance(), request, originalTransaction);
        Transaction refundTransaction = transactionFactory.createRefundTransaction(
                request,
                card,
                originalTransaction,
                refundResult
        );
        card.setBalance(refundResult.getNewBalance());
        transactionRepository.save(refundTransaction);
        cardRepository.save(card);
        logRefundSuccess(request, originalTransaction, refundResult.getNewBalance());
        return mapper.toBalanceResponse(card);
    }

    private Card getCardWithLock(String cardNumber) {
        return cardRepository.findByCardNumberWithLock(cardNumber)
                .orElseThrow(() -> new CardNotFoundException(cardNumber));
    }

    private Transaction validateOriginalTransaction(RefundRequest request, Card card) {
        Transaction originalTransaction = transactionRepository.findById(request.getOriginalTransactionId())
                .orElseThrow(() -> new InvalidOperationException(
                        "Транзакция с ID " + request.getOriginalTransactionId() + " не найдена"));
        if (!originalTransaction.getCard().getId().equals(card.getId())) {
            throw new InvalidOperationException("Транзакция не принадлежит указанной карте");
        }
        List<Transaction> refunds = transactionRepository.findRefundsForTransaction(originalTransaction.getId());
        if (!refunds.isEmpty()) {
            throw new InvalidOperationException("По транзакции уже был выполнен возврат");
        }
        return originalTransaction;
    }

    private void logRefundSuccess(RefundRequest request, Transaction originalTransaction, long newBalance) {
        log.info("Возврат выполнен: карта={}, оригинал={}({}), сумма={}, новый баланс={}",
                request.getCardNumber(),
                originalTransaction.getId(),
                originalTransaction.getType(),
                Math.abs(originalTransaction.getAmount()),
                newBalance);
    }
}
