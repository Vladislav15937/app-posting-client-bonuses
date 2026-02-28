package ru.retail.service.app.posting.client.bonuses.app.service;

import ru.retail.service.app.posting.client.bonuses.app.entity.Transaction;
import ru.retail.service.app.posting.client.bonuses.app.service.utils.RefundCalculationResult;

public interface RefundCalculatorService {

    RefundCalculationResult calculate(Long balance, Transaction originalTransaction);
}
