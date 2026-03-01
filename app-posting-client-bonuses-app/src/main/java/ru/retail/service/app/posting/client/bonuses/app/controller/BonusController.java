package ru.retail.service.app.posting.client.bonuses.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.retail.service.app.posting.client.bonuses.app.dto.request.BonusOperationRequest;
import ru.retail.service.app.posting.client.bonuses.app.dto.request.RefundRequest;
import ru.retail.service.app.posting.client.bonuses.app.dto.response.BalanceResponse;
import ru.retail.service.app.posting.client.bonuses.app.dto.response.ErrorResponse;
import ru.retail.service.app.posting.client.bonuses.app.dto.response.TransactionResponse;

import java.time.LocalDate;

@RestController
@RequestMapping("/v1/bonuses")
public interface BonusController {

    @Tag(name = "Управление бонусами", description = "API для управления бонусными картами и бонусами")
    @Operation(
            summary = "Начисление бонусов",
            description = "Начисляет бонусы на карту клиента. Сумма указывается в копейках."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Бонусы успешно начислены",
                    content = @Content(schema = @Schema(implementation = BalanceResponse.class))),
            @ApiResponse(responseCode = "400", description = "Неверный запрос",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Карта не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "429", description = "Слишком много запросов",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/accrue")
    ResponseEntity<BalanceResponse> accrueBonuses(
            @Valid @RequestBody
            @Parameter(description = "Данные для начисления бонусов", required = true)
            BonusOperationRequest request
    );

    @Tag(name = "Управление бонусами", description = "API для управления бонусными картами и бонусами")
    @Operation(
            summary = "Списание бонусов",
            description = "Списывает бонусы с карты клиента. Проверяет достаточность средств."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Бонусы успешно списаны",
                    content = @Content(schema = @Schema(implementation = BalanceResponse.class))),
            @ApiResponse(responseCode = "400", description = "Недостаточно средств или неверный запрос",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Карта не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "429", description = "Слишком много запросов",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/write-off")
    ResponseEntity<BalanceResponse> writeOffBonuses(
            @Valid @RequestBody
            @Parameter(description = "Данные для списания бонусов", required = true)
            BonusOperationRequest request
    );

    @Tag(name = "Управление бонусами", description = "API для управления бонусными картами и бонусами")
    @Operation(
            summary = "Возврат бонусов",
            description = "Возвращает ранее списанные или начисленные бонусы (например, при возврате товара)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Возврат успешно выполнен",
                    content = @Content(schema = @Schema(implementation = BalanceResponse.class))),
            @ApiResponse(responseCode = "400", description = "Неверный запрос или транзакция уже возвращена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Карта или транзакция не найдены",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "429", description = "Слишком много запросов",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/refund")
    ResponseEntity<BalanceResponse> refundBonuses(
            @Valid @RequestBody
            @Parameter(description = "Данные для возврата", required = true)
            RefundRequest request
    );

    @Tag(name = "Получение информации баланса лицевого счёта", description = "API для получения информации по лицевому счёту")
    @Operation(
            summary = "Получение баланса",
            description = "Возвращает текущий баланс карты в рублях и копейках"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Баланс успешно получен",
                    content = @Content(schema = @Schema(implementation = BalanceResponse.class))),
            @ApiResponse(responseCode = "404", description = "Карта не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "429", description = "Слишком много запросов",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/balance/{cardNumber}")
    ResponseEntity<BalanceResponse> getBalance(
            @Parameter(description = "Номер карты (10-20 цифр)", required = true, example = "1234567890")
            @PathVariable String cardNumber
    );

    @Tag(name = "История", description = "API для получения информации по истории операций лицевого счёта")
    @Operation(
            summary = "История операций",
            description = "Возвращает историю операций по карте с пагинацией"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "История успешно получена",
                    content = @Content(schema = @Schema(implementation = TransactionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Карта не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "429", description = "Слишком много запросов",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/history/{cardNumber}")
    ResponseEntity<Page<TransactionResponse>> getTransactionHistory(
            @Parameter(description = "Номер карты", required = true, example = "1234567890")
            @PathVariable String cardNumber,

            @Parameter(description = "Номер страницы (начиная с 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Размер страницы (макс 100)", example = "20")
            @RequestParam(defaultValue = "20") int size,

            @Parameter(description = "Сортировка (поле,направление)", example = "createdAt,desc")
            @RequestParam(defaultValue = "createdAt,desc") String sort);

    @Tag(name = "История", description = "API для получения информации по истории операций лицевого счёта")
    @Operation(
            summary = "История операций за период",
            description = "Возвращает историю операций по карте за указанный период с пагинацией"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "История успешно получена",
                    content = @Content(schema = @Schema(implementation = TransactionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Неверный формат даты",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Карта не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "429", description = "Слишком много запросов",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/history/{cardNumber}/period")
    ResponseEntity<Page<TransactionResponse>> getTransactionHistoryByPeriod(
            @Parameter(description = "Номер карты", required = true, example = "1234567890")
            @PathVariable String cardNumber,

            @Parameter(description = "Дата начала (формат: YYYY-MM-DD)", required = true, example = "2026-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @Parameter(description = "Дата окончания (формат: YYYY-MM-DD)", required = true, example = "2026-12-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

            @Parameter(description = "Номер страницы (начиная с 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Размер страницы (макс 100)", example = "20")
            @RequestParam(defaultValue = "20") int size,

            @Parameter(description = "Сортировка (поле,направление)", example = "createdAt,desc")
            @RequestParam(defaultValue = "createdAt,desc") String sort
    );
}