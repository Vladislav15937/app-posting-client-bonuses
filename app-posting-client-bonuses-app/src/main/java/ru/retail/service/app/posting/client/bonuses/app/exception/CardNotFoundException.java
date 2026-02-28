package ru.retail.service.app.posting.client.bonuses.app.exception;

public class CardNotFoundException extends RuntimeException {
    public CardNotFoundException(String cardNumber) {
        super("Карта с номером " + cardNumber + " не найдена");
    }
}
