package ru.retail.service.app.posting.client.bonuses.app.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.retail.service.app.posting.client.bonuses.app.exception.InvalidOperationException;
import ru.retail.service.app.posting.client.bonuses.app.repository.TransactionRepository;
import ru.retail.service.app.posting.client.bonuses.app.service.IdempotencyService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IdempotencyServiceImpl implements IdempotencyService {

    private final TransactionRepository transactionRepository;

    @Override
    public void checkIdempotency(String idempotencyKey) {
        if (idempotencyKey != null) {
            checkIdempotency(UUID.fromString(idempotencyKey));
        }
    }

    private void checkIdempotency(UUID idempotencyKey) {
        if (transactionRepository.existsByTransactionUuid(idempotencyKey)) {
            throw new InvalidOperationException(
                    "Операция с ключом идемпотентности " + idempotencyKey + " уже была выполнена");
        }
    }
}
