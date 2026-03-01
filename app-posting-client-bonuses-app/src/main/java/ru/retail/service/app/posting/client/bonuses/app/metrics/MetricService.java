package ru.retail.service.app.posting.client.bonuses.app.metrics;

public interface MetricService {

    void incrementSuccessRequest();

    void incrementSystemFail();
}
