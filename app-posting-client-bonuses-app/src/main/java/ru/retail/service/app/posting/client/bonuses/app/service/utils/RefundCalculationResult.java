package ru.retail.service.app.posting.client.bonuses.app.service.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.retail.service.app.posting.client.bonuses.app.entity.Transaction;

@Data
@AllArgsConstructor
public class RefundCalculationResult {

    private final long oldBalance;
    private final long newBalance;
    private final long refundAmount;
    private final long signedAmount;
    private final Transaction.TransactionType originalType;
}
