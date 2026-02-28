package ru.retail.service.app.posting.client.bonuses.app.metrics.name;

public class BonusControllerMetrics {

    private BonusControllerMetrics() {
    }

    public static final String TAG_OPERATION = "operation";
    public static final String TOTAL = "_total";
    public static final String SUCCESS = "_success";
    public static final String SYSTEM_FAIL = "_system_fail";
    public static final String TIME = "_time";
    public static final String TOTAL_REQUESTS_DESC = "Общее количество запросов";
    public static final String SUCCESS_REQUESTS_DESC = "Количество успешно обработанных запросов";
    public static final String SYSTEM_FAIL_DESC = "Количество запросов, обработанных с ошибкой";
    public static final String TIME_DESC = "Длительность обработки запроса";

    //accrueBonuses
    public static final String ACCRUE_BONUSES_PREFIX = "hrp_app_posting_client_bonuses_accrue_bonuses";
    public static final String ACCRUE_BONUSES_TOTAL_REQUESTS = ACCRUE_BONUSES_PREFIX + TOTAL;
    public static final String ACCRUE_BONUSES_SUCCESS_REQUESTS = ACCRUE_BONUSES_PREFIX + SUCCESS;
    public static final String ACCRUE_BONUSES_SYSTEM_FAIL = ACCRUE_BONUSES_PREFIX + SYSTEM_FAIL;
    public static final String ACCRUE_BONUSES_PROCESSING_TIME = ACCRUE_BONUSES_PREFIX + TIME;
    public static final String ACCRUE_BONUSES_OPERATION_VALUE = "accrue-bonuses";

    //writeOffBonuses
    public static final String WRITE_OFF_BONUSES_PREFIX = "hrp_app_posting_client_bonuses_write_off_bonuses";
    public static final String WRITE_OFF_BONUSES_TOTAL_REQUESTS = WRITE_OFF_BONUSES_PREFIX + TOTAL;
    public static final String WRITE_OFF_BONUSES_SUCCESS_REQUESTS = WRITE_OFF_BONUSES_PREFIX + SUCCESS;
    public static final String WRITE_OFF_BONUSES_SYSTEM_FAIL = WRITE_OFF_BONUSES_PREFIX + SYSTEM_FAIL;
    public static final String WRITE_OFF_BONUSES_PROCESSING_TIME = WRITE_OFF_BONUSES_PREFIX + TIME;
    public static final String WRITE_OFF_BONUSES_OPERATION_VALUE = "write-off-bonuses";

    //refundBonuses
    public static final String REFUND_BONUSES_PREFIX = "hrp_app_posting_client_bonuses_refund_bonuses";
    public static final String REFUND_BONUSES_TOTAL_REQUESTS = REFUND_BONUSES_PREFIX + TOTAL;
    public static final String REFUND_BONUSES_SUCCESS_REQUESTS = REFUND_BONUSES_PREFIX + SUCCESS;
    public static final String REFUND_BONUSES_SYSTEM_FAIL = REFUND_BONUSES_PREFIX + SYSTEM_FAIL;
    public static final String REFUND_BONUSES_PROCESSING_TIME = REFUND_BONUSES_PREFIX + TIME;
    public static final String REFUND_BONUSES_OPERATION_VALUE = "refund-bonuses";

    //getBalance
    public static final String GET_BALANCE_BONUSES_PREFIX = "hrp_app_posting_client_bonuses_get_balance";
    public static final String GET_BALANCE_BONUSES_TOTAL_REQUESTS = GET_BALANCE_BONUSES_PREFIX + TOTAL;
    public static final String GET_BALANCE_BONUSES_SUCCESS_REQUESTS = GET_BALANCE_BONUSES_PREFIX + SUCCESS;
    public static final String GET_BALANCE_BONUSES_SYSTEM_FAIL = GET_BALANCE_BONUSES_PREFIX + SYSTEM_FAIL;
    public static final String GET_BALANCE_BONUSES_PROCESSING_TIME = GET_BALANCE_BONUSES_PREFIX + TIME;
    public static final String GET_BALANCE_BONUSES_OPERATION_VALUE = "get-balance";

    //getTransactionHistory
    public static final String GET_TRANSACTION_HISTORY_BONUSES_PREFIX = "hrp_app_posting_client_bonuses_get_transaction_history";
    public static final String GET_TRANSACTION_HISTORY_BONUSES_TOTAL_REQUESTS = GET_TRANSACTION_HISTORY_BONUSES_PREFIX + TOTAL;
    public static final String GET_TRANSACTION_HISTORY_BONUSES_SUCCESS_REQUESTS = GET_TRANSACTION_HISTORY_BONUSES_PREFIX + SUCCESS;
    public static final String GET_TRANSACTION_HISTORY_BONUSES_SYSTEM_FAIL = GET_TRANSACTION_HISTORY_BONUSES_PREFIX + SYSTEM_FAIL;
    public static final String GET_TRANSACTION_HISTORY_BONUSES_PROCESSING_TIME = GET_TRANSACTION_HISTORY_BONUSES_PREFIX + TIME;
    public static final String GET_TRANSACTION_HISTORY_BONUSES_OPERATION_VALUE = "get-transaction-history";

    //getTransactionHistoryByPeriod
    public static final String GET_TRANSACTION_HISTORY_BY_PERIOD_BONUSES_PREFIX = "hrp_app_posting_client_bonuses_get_transaction_history_by_period";
    public static final String GET_TRANSACTION_HISTORY_BY_PERIOD_BONUSES_TOTAL_REQUESTS = GET_TRANSACTION_HISTORY_BY_PERIOD_BONUSES_PREFIX + TOTAL;
    public static final String GET_TRANSACTION_HISTORY_BY_PERIOD_BONUSES_SUCCESS_REQUESTS = GET_TRANSACTION_HISTORY_BY_PERIOD_BONUSES_PREFIX + SUCCESS;
    public static final String GET_TRANSACTION_HISTORY_BY_PERIOD_BONUSES_SYSTEM_FAIL = GET_TRANSACTION_HISTORY_BY_PERIOD_BONUSES_PREFIX + SYSTEM_FAIL;
    public static final String GET_TRANSACTION_HISTORY_BY_PERIOD_BONUSES_PROCESSING_TIME = GET_TRANSACTION_HISTORY_BY_PERIOD_BONUSES_PREFIX + TIME;
    public static final String GET_TRANSACTION_HISTORY_BY_PERIOD_BONUSES_OPERATION_VALUE = "get-transaction-history-by-period";
}
