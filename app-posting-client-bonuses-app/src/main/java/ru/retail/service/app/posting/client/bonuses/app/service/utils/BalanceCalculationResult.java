package ru.retail.service.app.posting.client.bonuses.app.service.utils;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BalanceCalculationResult {

    private final long oldBalance;
    private final long newBalance;
    private final long amount;
}
