package ru.retail.service.app.posting.client.bonuses.app.service;

import ru.retail.service.app.posting.client.bonuses.app.service.utils.BalanceCalculationResult;
import ru.retail.service.app.posting.client.bonuses.app.service.utils.OperationType;

public interface BalanceCalculatorService {

    BalanceCalculationResult calculate(Long balance, Long amount, OperationType operationType);
}
