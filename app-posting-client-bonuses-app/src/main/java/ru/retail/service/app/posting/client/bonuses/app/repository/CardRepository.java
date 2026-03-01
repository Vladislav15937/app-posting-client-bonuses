package ru.retail.service.app.posting.client.bonuses.app.repository;

import io.micrometer.observation.annotation.Observed;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.retail.service.app.posting.client.bonuses.app.entity.Card;

import java.util.Optional;

@Repository
@Observed
public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findByCardNumber(String cardNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000"))
    @Query("SELECT c FROM Card c WHERE c.cardNumber = :cardNumber")
    Optional<Card> findByCardNumberWithLock(@Param("cardNumber") String cardNumber);
}
