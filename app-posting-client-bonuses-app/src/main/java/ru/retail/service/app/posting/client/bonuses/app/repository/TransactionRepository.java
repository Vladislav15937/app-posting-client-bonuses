package ru.retail.service.app.posting.client.bonuses.app.repository;

import io.micrometer.observation.annotation.Observed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.retail.service.app.posting.client.bonuses.app.entity.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
@Observed
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t WHERE t.parentTransaction.id = :transactionId")
    List<Transaction> findRefundsForTransaction(@Param("transactionId") Long transactionId);

    boolean existsByTransactionUuid(UUID transactionUuid);

    Page<Transaction> findByCard_CardNumber(String cardNumber, Pageable pageable);

    Page<Transaction> findByCard_CardNumberAndCreatedAtBetween(
            String cardNumber,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable);
}
