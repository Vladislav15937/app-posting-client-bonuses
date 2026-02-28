package ru.retail.service.app.posting.client.bonuses.app.service;

import ru.retail.service.app.posting.client.bonuses.app.dto.response.BalanceResponse;

public interface BalanceService {

    BalanceResponse getBalance(String cardNumber);
}
