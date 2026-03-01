package ru.retail.service.app.posting.client.bonuses.app.service.impl;

import org.springframework.stereotype.Service;
import ru.retail.service.app.posting.client.bonuses.app.dto.request.BonusOperationRequest;
import ru.retail.service.app.posting.client.bonuses.app.dto.request.RefundRequest;
import ru.retail.service.app.posting.client.bonuses.app.entity.Card;
import ru.retail.service.app.posting.client.bonuses.app.entity.Transaction;
import ru.retail.service.app.posting.client.bonuses.app.service.TransactionFactoryService;
import ru.retail.service.app.posting.client.bonuses.app.service.utils.BalanceCalculationResult;
import ru.retail.service.app.posting.client.bonuses.app.service.utils.RefundCalculationResult;

import java.util.UUID;

@Service
public class TransactionFactoryServiceImpl implements TransactionFactoryService {

    @Override
    public Transaction createOperationTransaction(
            Card card,
            long amount,
            Transaction.TransactionType type,
            BalanceCalculationResult balance,
            BonusOperationRequest request) {
        Transaction transaction = new Transaction();
        transaction.setCard(card);
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setBalanceBefore(balance.getOldBalance());
        transaction.setBalanceAfter(balance.getNewBalance());
        transaction.setDescription(request.getDescription());
        transaction.setExternalId(request.getExternalId());
        transaction.setExternalSystem(request.getExternalSystem());
        transaction.setCreatedBy("SYSTEM");
        if (request.getIdempotencyKey() != null) {
            transaction.setTransactionUuid(UUID.fromString(request.getIdempotencyKey()));
        }
        return transaction;
    }

    @Override
    public Transaction createRefundTransaction(
            RefundRequest request,
            Card card,
            Transaction originalTransaction,
            RefundCalculationResult refundResult) {
        Transaction refundTransaction = new Transaction();
        refundTransaction.setCard(card);
        refundTransaction.setAmount(refundResult.getSignedAmount());
        refundTransaction.setType(Transaction.TransactionType.REFUND);
        refundTransaction.setBalanceBefore(refundResult.getOldBalance());
        refundTransaction.setBalanceAfter(refundResult.getNewBalance());
        refundTransaction.setDescription(request.getDescription() != null ?
                request.getDescription() : "Возврат по транзакции #" + originalTransaction.getId());
        refundTransaction.setParentTransaction(originalTransaction);
        refundTransaction.setCreatedBy("SYSTEM");
        if (request.getIdempotencyKey() != null) {
            refundTransaction.setTransactionUuid(request.getIdempotencyKey());
        }
        return refundTransaction;
    }
}
