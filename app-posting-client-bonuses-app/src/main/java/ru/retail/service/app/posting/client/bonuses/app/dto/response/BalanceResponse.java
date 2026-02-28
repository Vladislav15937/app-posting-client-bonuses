package ru.retail.service.app.posting.client.bonuses.app.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "Информация о балансе карты")
public class BalanceResponse {

    @Schema(description = "Номер карты", example = "1234567890")
    private String cardNumber;

    @Schema(description = "Баланс в рублях", example = "100.50")
    private BigDecimal balance;

    @Schema(description = "Баланс в копейках", example = "10050")
    private Long balanceInKopecks;

    @Schema(description = "Статус карты", example = "ACTIVE",
            allowableValues = {"ACTIVE", "BLOCKED", "CLOSED"})
    private String status;

    @Schema(description = "Время запроса", example = "2026-02-26T15:30:00")
    private LocalDateTime timestamp;
}
