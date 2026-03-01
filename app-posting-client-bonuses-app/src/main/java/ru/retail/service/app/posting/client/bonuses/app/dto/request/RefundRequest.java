package ru.retail.service.app.posting.client.bonuses.app.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.retail.service.app.posting.client.bonuses.app.utils.sanitize.SanitizedStringDeserializer;

import java.util.UUID;

@Data
@Schema(description = "Запрос на возврат бонусов")
public class RefundRequest {

    @Schema(description = "Номер карты",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "1234567890")
    @NotBlank(message = "Номер карты обязателен")
    @Pattern(regexp = "^[0-9]{10,20}$", message = "Номер карты должен содержать от 10 до 20 цифр")
    private String cardNumber;

    @Schema(description = "ID оригинальной транзакции",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "42")
    @NotNull(message = "ID оригинальной транзакции обязателен")
    @Positive(message = "ID транзакции должен быть положительным")
    private Long originalTransactionId;

    @Schema(description = "Причина возврата",
            example = "Возврат товара по чеку №12345")
    @Size(max = 500, message = "Описание не должно превышать 500 символов")
    @JsonDeserialize(using = SanitizedStringDeserializer.class)
    private String description;

    @Schema(description = "Ключ идемпотентности",
            example = "660e8400-e29b-41d4-a716-446655440001")
    @NotNull(message = "Ключ идемпотентности обязателен")
    private UUID idempotencyKey;
}
