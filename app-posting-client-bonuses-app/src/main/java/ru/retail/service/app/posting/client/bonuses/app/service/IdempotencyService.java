package ru.retail.service.app.posting.client.bonuses.app.service;

public interface IdempotencyService {

    void checkIdempotency(String idempotencyKey);
}
