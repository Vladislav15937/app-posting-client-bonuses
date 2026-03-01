package ru.retail.service.app.posting.client.bonuses.app.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Schema(description = "Ответ с ошибкой")
public class ErrorResponse {

    @Schema(description = "HTTP статус код", example = "400")
    private int status;

    @Schema(description = "Сообщение об ошибке",
            example = "Недостаточно средств на карте")
    private String message;

    @Schema(description = "Время ошибки", example = "2026-02-26T15:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "Путь запроса", example = "/api/v1/bonuses/write-off")
    private String path;
}