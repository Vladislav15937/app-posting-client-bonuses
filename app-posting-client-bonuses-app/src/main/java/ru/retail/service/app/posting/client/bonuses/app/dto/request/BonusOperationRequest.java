package ru.retail.service.app.posting.client.bonuses.app.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.retail.service.app.posting.client.bonuses.app.utils.sanitize.SanitizedStringDeserializer;

@Data
@Schema(description = "Запрос на операцию с бонусами")
public class BonusOperationRequest {

    @Schema(description = "Номер карты (10-20 цифр)",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "1234567890")
    @NotBlank(message = "Номер карты обязателен")
    @Pattern(regexp = "^[0-9]{10,20}$", message = "Номер карты должен содержать от 10 до 20 цифр")
    private String cardNumber;

    @Schema(description = "Сумма в копейках (минимальная 1 копейка)",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "1000")
    @NotNull(message = "Сумма обязательна")
    @Min(value = 1, message = "Сумма должна быть больше 0")
    @Positive(message = "Сумма должна быть положительной")
    @Max(value = 10000000)
    private Long amount;

    @Schema(description = "Описание операции",
            example = "Покупка в магазине 'Продукты'")
    @Size(max = 500, message = "Описание не должно превышать 500 символов")
    @JsonDeserialize(using = SanitizedStringDeserializer.class)
    private String description;

    @Schema(description = "Ключ идемпотентности для защиты от двойных списаний",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "550e8400-e29b-41d4-a716-446655440000")
    @NotNull(message = "Ключ идемпотентности обязателен")
    @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$",
            message = "Неверный формат UUID")
    private String idempotencyKey;

    @Schema(description = "Идентификатор во внешней системе",
            example = "order_12345")
    private String externalId;

    @Schema(description = "Название внешней системы",
            example = "online_store")
    private String externalSystem;
}
