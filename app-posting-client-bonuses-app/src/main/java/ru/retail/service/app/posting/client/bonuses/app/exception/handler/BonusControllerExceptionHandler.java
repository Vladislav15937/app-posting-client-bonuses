package ru.retail.service.app.posting.client.bonuses.app.exception.handler;

import io.micrometer.observation.annotation.Observed;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.retail.service.app.posting.client.bonuses.app.controller.BonusController;
import ru.retail.service.app.posting.client.bonuses.app.dto.response.ErrorResponse;
import ru.retail.service.app.posting.client.bonuses.app.exception.CardNotFoundException;
import ru.retail.service.app.posting.client.bonuses.app.exception.InsufficientBalanceException;
import ru.retail.service.app.posting.client.bonuses.app.exception.InvalidOperationException;
import ru.retail.service.app.posting.client.bonuses.app.metrics.MetricService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(assignableTypes = {BonusController.class})
@Slf4j
@Observed
public class BonusControllerExceptionHandler {

    private static final String LOG_CARD_NOT_FOUND = "Card not found: {}";
    private static final String LOG_INSUFFICIENT_BALANCE = "Insufficient balance: {}";
    private static final String LOG_INVALID_OPERATION = "Invalid operation: {}";
    private static final String LOG_VALIDATION_ERROR = "Validation error: {}";
    private static final String LOG_DUPLICATE_KEY = "Duplicate key detected: {}";
    private static final String LOG_GENERIC_ERROR = "Unexpected error: {}";
    private static final String MSG_CARD_NOT_FOUND_BALANCE = "Карта не найдена. Невозможно проверить баланс.";
    private static final String MSG_CARD_NOT_FOUND_HISTORY = "Карта не найдена. История операций недоступна.";
    private static final String MSG_CARD_NOT_FOUND_HISTORY_PERIOD = "Карта не найдена. История операций за период недоступна.";
    private static final String MSG_CARD_NOT_FOUND_ACCRUE = "Карта не найдена. Для начисления бонусов карта будет создана автоматически.";
    private static final String MSG_CARD_NOT_FOUND_WRITE_OFF = "Карта не найдена. Списание невозможно без существующей карты.";
    private static final String MSG_CARD_NOT_FOUND_REFUND = "Карта не найдена. Возврат невозможен без существующей карты.";
    private static final String MSG_CARD_NOT_FOUND_DEFAULT = "Карта не найдена: ";
    private static final String MSG_INSUFFICIENT_BALANCE_WRITE_OFF = "Недостаточно бонусов для списания. ";
    private static final String MSG_INSUFFICIENT_BALANCE_REFUND = "Недостаточно бонусов для возврата. ";
    private static final String MSG_INSUFFICIENT_BALANCE_DEFAULT = "Недостаточно бонусов: ";
    private static final String MSG_INVALID_OPERATION_ACCRUE = "Невозможно выполнить начисление: ";
    private static final String MSG_INVALID_OPERATION_WRITE_OFF = "Невозможно выполнить списание: ";
    private static final String MSG_INVALID_OPERATION_REFUND = "Невозможно выполнить возврат: ";
    private static final String MSG_INVALID_OPERATION_DEFAULT = "Некорректная операция: ";
    private static final String MSG_VALIDATION_BONUS_OPERATION = "Ошибка в данных операции с бонусами: ";
    private static final String MSG_VALIDATION_REFUND = "Ошибка в данных возврата: ";
    private static final String MSG_DUPLICATE_IDEMPOTENCY_KEY = "Операция с таким ключом идемпотентности уже была выполнена";
    private static final String MSG_DUPLICATE_CARD_NUMBER = "Карта с таким номером уже существует";
    private static final String MSG_DATA_INTEGRITY = "Ошибка целостности данных";
    private static final String MSG_HTTP_MESSAGE_NOT_READABLE = "Неверный формат запроса. Проверьте JSON.";
    private static final String MSG_GENERIC_READ_ERROR = "Ошибка при чтении данных. Пожалуйста, повторите попытку позже.";
    private static final String MSG_GENERIC_WRITE_ERROR = "Ошибка при выполнении операции. Пожалуйста, повторите попытку позже.";
    private static final String MSG_GENERIC_ERROR = "Внутренняя ошибка сервера: ";
    private static final Map<OperationType, String> INSUFFICIENT_BALANCE_MESSAGES = Map.of(
            OperationType.WRITE_OFF, MSG_INSUFFICIENT_BALANCE_WRITE_OFF,
            OperationType.REFUND, MSG_INSUFFICIENT_BALANCE_REFUND
    );
    private static final Map<OperationType, String> INVALID_OPERATION_MESSAGES = Map.of(
            OperationType.ACCRUE, MSG_INVALID_OPERATION_ACCRUE,
            OperationType.WRITE_OFF, MSG_INVALID_OPERATION_WRITE_OFF,
            OperationType.REFUND, MSG_INVALID_OPERATION_REFUND
    );
    private static final Map<OperationType, String> CARD_NOT_FOUND_MESSAGES = Map.of(
            OperationType.BALANCE, MSG_CARD_NOT_FOUND_BALANCE,
            OperationType.HISTORY, MSG_CARD_NOT_FOUND_HISTORY,
            OperationType.HISTORY_PERIOD, MSG_CARD_NOT_FOUND_HISTORY_PERIOD,
            OperationType.ACCRUE, MSG_CARD_NOT_FOUND_ACCRUE,
            OperationType.WRITE_OFF, MSG_CARD_NOT_FOUND_WRITE_OFF,
            OperationType.REFUND, MSG_CARD_NOT_FOUND_REFUND
    );

    private final Map<OperationType, MetricService> metricServices;

    public BonusControllerExceptionHandler(
            MetricService accrueMetricService,
            MetricService getBalanceMetricService,
            MetricService getTransactionHistoryByPeriodMetricService,
            MetricService getTransactionHistoryMetricService,
            MetricService refundBonusesMetricService,
            MetricService writeOffBonusesMetricService) {

        this.metricServices = Map.of(
                OperationType.ACCRUE, accrueMetricService,
                OperationType.BALANCE, getBalanceMetricService,
                OperationType.HISTORY, getTransactionHistoryMetricService,
                OperationType.HISTORY_PERIOD, getTransactionHistoryByPeriodMetricService,
                OperationType.REFUND, refundBonusesMetricService,
                OperationType.WRITE_OFF, writeOffBonusesMetricService
        );
    }

    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCardNotFound(
            CardNotFoundException e,
            HttpServletRequest request) {

        String path = request.getRequestURI();
        OperationType operationType = determineOperationType(path);

        log.debug(LOG_CARD_NOT_FOUND, e.getMessage());
        incrementSystemFail(operationType);

        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                getMessage(CARD_NOT_FOUND_MESSAGES, operationType, MSG_CARD_NOT_FOUND_DEFAULT + e.getMessage()),
                path
        );
    }

    @ExceptionHandler({
            InsufficientBalanceException.class,
            InvalidOperationException.class
    })
    public ResponseEntity<ErrorResponse> handleBusinessException(
            RuntimeException e,
            HttpServletRequest request) {

        String path = request.getRequestURI();
        OperationType operationType = determineOperationType(path);
        String baseMessage;
        if (e instanceof InsufficientBalanceException) {
            log.warn(LOG_INSUFFICIENT_BALANCE, e.getMessage());
            baseMessage = getMessage(INSUFFICIENT_BALANCE_MESSAGES, operationType, MSG_INSUFFICIENT_BALANCE_DEFAULT);
        } else {
            log.warn(LOG_INVALID_OPERATION, e.getMessage());
            baseMessage = getMessage(INVALID_OPERATION_MESSAGES, operationType, MSG_INVALID_OPERATION_DEFAULT);
        }
        incrementSystemFail(operationType);
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                baseMessage + e.getMessage(),
                path
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException e,
            HttpServletRequest request) {

        String path = request.getRequestURI();
        OperationType operationType = determineOperationType(path);

        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.debug(LOG_VALIDATION_ERROR, errors);
        incrementSystemFail(operationType);

        String message = isRefundOperation(operationType)
                ? MSG_VALIDATION_REFUND + errors
                : MSG_VALIDATION_BONUS_OPERATION + errors;

        return buildErrorResponse(HttpStatus.BAD_REQUEST, message, path);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException e,
            HttpServletRequest request) {

        String path = request.getRequestURI();
        OperationType operationType = determineOperationType(path);
        String message = e.getMostSpecificCause().getMessage();

        log.error(LOG_DUPLICATE_KEY, message);
        incrementSystemFail(operationType);

        String errorMessage;
        if (message.contains("transaction_uuid")) {
            errorMessage = MSG_DUPLICATE_IDEMPOTENCY_KEY;
        } else if (message.contains("card_number")) {
            errorMessage = MSG_DUPLICATE_CARD_NUMBER;
        } else {
            errorMessage = MSG_DATA_INTEGRITY;
        }

        return buildErrorResponse(HttpStatus.CONFLICT, errorMessage, path);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpServletRequest request) {

        String path = request.getRequestURI();
        return buildErrorResponse(HttpStatus.BAD_REQUEST, MSG_HTTP_MESSAGE_NOT_READABLE, path);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception e,
            HttpServletRequest request) {

        String path = request.getRequestURI();
        OperationType operationType = determineOperationType(path);

        log.error(LOG_GENERIC_ERROR, e.getMessage(), e);
        incrementSystemFail(operationType);

        String message = isReadOperation(operationType)
                ? MSG_GENERIC_READ_ERROR
                : isWriteOperation(operationType)
                ? MSG_GENERIC_WRITE_ERROR
                : MSG_GENERIC_ERROR + e.getMessage();

        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, message, path);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message, String path) {
        ErrorResponse error = new ErrorResponse(
                status.value(),
                message,
                LocalDateTime.now(),
                path
        );
        return ResponseEntity.status(status).body(error);
    }

    private void incrementSystemFail(OperationType operationType) {
        MetricService metricService = metricServices.get(operationType);
        if (metricService != null) {
            metricService.incrementSystemFail();
        }
    }

    private String getMessage(Map<OperationType, String> messages, OperationType operationType, String defaultValue) {
        return messages.getOrDefault(operationType, defaultValue);
    }

    private boolean isReadOperation(OperationType operationType) {
        return operationType == OperationType.BALANCE
                || operationType == OperationType.HISTORY
                || operationType == OperationType.HISTORY_PERIOD;
    }

    private boolean isWriteOperation(OperationType operationType) {
        return operationType == OperationType.ACCRUE
                || operationType == OperationType.WRITE_OFF
                || operationType == OperationType.REFUND;
    }

    private boolean isRefundOperation(OperationType operationType) {
        return operationType == OperationType.REFUND;
    }

    private OperationType determineOperationType(String path) {
        if (path.contains("/balance")) return OperationType.BALANCE;
        if (path.contains("/history")) {
            return path.contains("/period") ? OperationType.HISTORY_PERIOD : OperationType.HISTORY;
        }
        if (path.contains("/accrue")) return OperationType.ACCRUE;
        if (path.contains("/write-off")) return OperationType.WRITE_OFF;
        if (path.contains("/refund")) return OperationType.REFUND;
        return OperationType.UNKNOWN;
    }

    private enum OperationType {
        BALANCE, HISTORY, HISTORY_PERIOD, ACCRUE, WRITE_OFF, REFUND, UNKNOWN
    }
}
