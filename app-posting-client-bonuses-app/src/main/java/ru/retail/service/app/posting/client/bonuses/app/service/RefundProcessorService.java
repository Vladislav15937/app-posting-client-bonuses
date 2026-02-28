package ru.retail.service.app.posting.client.bonuses.app.service;

import ru.retail.service.app.posting.client.bonuses.app.dto.request.RefundRequest;
import ru.retail.service.app.posting.client.bonuses.app.dto.response.BalanceResponse;

public interface RefundProcessorService {

    BalanceResponse process(RefundRequest request);
}
