package ru.retail.service.app.posting.client.bonuses.app.service.impl;

import org.springframework.stereotype.Service;
import ru.retail.service.app.posting.client.bonuses.app.service.BalanceCalculatorService;
import ru.retail.service.app.posting.client.bonuses.app.service.utils.BalanceCalculationResult;
import ru.retail.service.app.posting.client.bonuses.app.service.utils.OperationType;

@Service
public class BalanceCalculatorServiceImpl implements BalanceCalculatorService {

    @Override
    public BalanceCalculationResult calculate(Long oldBalance, Long amount, OperationType operationType) {
        long newBalance = operationType.calculateNewBalance(oldBalance, amount);
        return new BalanceCalculationResult(oldBalance, newBalance, amount);
    }
}
