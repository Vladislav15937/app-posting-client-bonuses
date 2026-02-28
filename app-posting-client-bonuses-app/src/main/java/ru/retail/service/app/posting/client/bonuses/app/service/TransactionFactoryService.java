package ru.retail.service.app.posting.client.bonuses.app.service;

import ru.retail.service.app.posting.client.bonuses.app.dto.request.BonusOperationRequest;
import ru.retail.service.app.posting.client.bonuses.app.dto.request.RefundRequest;
import ru.retail.service.app.posting.client.bonuses.app.entity.Card;
import ru.retail.service.app.posting.client.bonuses.app.entity.Transaction;
import ru.retail.service.app.posting.client.bonuses.app.service.utils.BalanceCalculationResult;
import ru.retail.service.app.posting.client.bonuses.app.service.utils.RefundCalculationResult;

public interface TransactionFactoryService {

    Transaction createOperationTransaction(Card card,
                                           long signedAmount,
                                           Transaction.TransactionType transactionType,
                                           BalanceCalculationResult balanceResult,
                                           BonusOperationRequest request);

    Transaction createRefundTransaction(RefundRequest request, Card card,
                                        Transaction originalTransaction,
                                        RefundCalculationResult refundResult);
}
