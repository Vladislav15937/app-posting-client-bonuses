package ru.retail.service.app.posting.client.bonuses.app.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Schema(description = "Информация о транзакции")
public class TransactionResponse {

    @Schema(description = "ID транзакции", example = "1")
    private Long id;

    @Schema(description = "UUID транзакции",
            example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID transactionUuid;

    @Schema(description = "Номер карты", example = "1234567890")
    private String cardNumber;

    @Schema(description = "Сумма в рублях", example = "10.00")
    private BigDecimal amount;

    @Schema(description = "Сумма в копейках", example = "1000")
    private Long amountInKopecks;

    @Schema(description = "Тип операции",
            allowableValues = {"ACCRUAL", "WRITE_OFF", "REFUND", "CORRECTION"})
    private String type;

    @Schema(description = "Баланс до операции в рублях", example = "100.00")
    private BigDecimal balanceBefore;

    @Schema(description = "Баланс после операции в рублях", example = "110.00")
    private BigDecimal balanceAfter;

    @Schema(description = "Описание операции",
            example = "Начисление за покупку")
    private String description;

    @Schema(description = "Статус транзакции",
            allowableValues = {"PENDING", "COMPLETED", "FAILED", "CANCELLED"})
    private String status;

    @Schema(description = "Время создания", example = "2026-02-26T15:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "ID родительской транзакции (для возвратов)", example = "1")
    private Long parentTransactionId;
}
