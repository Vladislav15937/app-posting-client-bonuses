package ru.retail.service.app.posting.client.bonuses.app.metrics.impl;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;
import ru.retail.service.app.posting.client.bonuses.app.metrics.MetricService;

import static ru.retail.service.app.posting.client.bonuses.app.metrics.name.BonusControllerMetrics.SUCCESS_REQUESTS_DESC;
import static ru.retail.service.app.posting.client.bonuses.app.metrics.name.BonusControllerMetrics.SYSTEM_FAIL_DESC;
import static ru.retail.service.app.posting.client.bonuses.app.metrics.name.BonusControllerMetrics.TAG_OPERATION;
import static ru.retail.service.app.posting.client.bonuses.app.metrics.name.BonusControllerMetrics.WRITE_OFF_BONUSES_OPERATION_VALUE;
import static ru.retail.service.app.posting.client.bonuses.app.metrics.name.BonusControllerMetrics.WRITE_OFF_BONUSES_SUCCESS_REQUESTS;
import static ru.retail.service.app.posting.client.bonuses.app.metrics.name.BonusControllerMetrics.WRITE_OFF_BONUSES_SYSTEM_FAIL;

@Service("writeOffBonusesMetricService")
public class WriteOffBonusesControllerMetricServiceImpl implements MetricService {

    private final Counter controllerSuccessCounter;
    private final Counter controllerSystemFail;

    public WriteOffBonusesControllerMetricServiceImpl(MeterRegistry meterRegistry) {
        this.controllerSuccessCounter = createCounter(meterRegistry,
                WRITE_OFF_BONUSES_SUCCESS_REQUESTS,
                SUCCESS_REQUESTS_DESC
        );

        this.controllerSystemFail = createCounter(meterRegistry,
                WRITE_OFF_BONUSES_SYSTEM_FAIL,
                SYSTEM_FAIL_DESC
        );
    }

    @Override
    public void incrementSuccessRequest() {
        controllerSuccessCounter.increment();
    }

    @Override
    public void incrementSystemFail() {
        controllerSystemFail.increment();
    }

    private static Counter createCounter(
            MeterRegistry meterRegistry,
            String name,
            String description) {
        return Counter.builder(name)
                .description(description)
                .tags(TAG_OPERATION, WRITE_OFF_BONUSES_OPERATION_VALUE)
                .register(meterRegistry);
    }
}
