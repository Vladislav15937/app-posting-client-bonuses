package ru.retail.service.app.posting.client.bonuses.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.retail.service.app.posting.client.bonuses.app.dto.response.BalanceResponse;
import ru.retail.service.app.posting.client.bonuses.app.entity.Card;
import ru.retail.service.app.posting.client.bonuses.app.exception.CardNotFoundException;
import ru.retail.service.app.posting.client.bonuses.app.mapper.BonusMapper;
import ru.retail.service.app.posting.client.bonuses.app.repository.CardRepository;
import ru.retail.service.app.posting.client.bonuses.app.service.BalanceService;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BalanceServiceImpl implements BalanceService {

    private final CardRepository cardRepository;
    private final BonusMapper mapper;

    @Override
    public BalanceResponse getBalance(String cardNumber) {
        log.info("Запрос баланса: карта={}", cardNumber);
        Card card = cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new CardNotFoundException(cardNumber));
        return mapper.toBalanceResponse(card);
    }
}
