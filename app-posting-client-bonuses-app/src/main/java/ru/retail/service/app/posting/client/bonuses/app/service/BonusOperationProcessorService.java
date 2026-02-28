package ru.retail.service.app.posting.client.bonuses.app.service;

import ru.retail.service.app.posting.client.bonuses.app.dto.request.BonusOperationRequest;
import ru.retail.service.app.posting.client.bonuses.app.dto.response.BalanceResponse;
import ru.retail.service.app.posting.client.bonuses.app.service.utils.OperationType;

public interface BonusOperationProcessorService {

    BalanceResponse process(BonusOperationRequest request, OperationType operationType, boolean b);
}
