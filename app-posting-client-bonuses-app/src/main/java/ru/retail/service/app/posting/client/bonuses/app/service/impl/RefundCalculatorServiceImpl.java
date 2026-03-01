package ru.retail.service.app.posting.client.bonuses.app.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.retail.service.app.posting.client.bonuses.app.entity.Transaction;
import ru.retail.service.app.posting.client.bonuses.app.exception.InvalidOperationException;
import ru.retail.service.app.posting.client.bonuses.app.service.RefundCalculatorService;
import ru.retail.service.app.posting.client.bonuses.app.service.utils.RefundCalculationResult;

@Service
@Slf4j
public class RefundCalculatorServiceImpl implements RefundCalculatorService {

    @Override
    public RefundCalculationResult calculate(Long currentBalance, Transaction original) {
        long amount = Math.abs(original.getAmount());
        long newBalance = switch (original.getType()) {
            case ACCRUAL -> {
                log.debug("Возврат начисления: -{}", amount);
                yield currentBalance - amount;
            }
            case WRITE_OFF -> {
                log.debug("Возврат списания: +{}", amount);
                yield currentBalance + amount;
            }
            default -> throw new InvalidOperationException(
                    "Нельзя вернуть транзакцию типа: " + original.getType());
        };

        return new RefundCalculationResult(
                currentBalance,
                newBalance,
                amount,
                -original.getAmount(),
                original.getType()
        );
    }
}
