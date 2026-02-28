package ru.retail.service.app.posting.client.bonuses.app.exception;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String cardNumber, Long requested, Long available) {
        super(String.format("Недостаточно средств на карте %s. Запрошено: %d коп., доступно: %d коп.",
                cardNumber, requested, available));
    }
}
