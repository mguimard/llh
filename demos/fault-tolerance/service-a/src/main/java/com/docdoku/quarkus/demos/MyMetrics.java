package com.docdoku.quarkus.demos;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
@Startup
public class MyMetrics {

    @Inject
    MeterRegistry metrics;

    @PostConstruct
    public void init() {
        // ...
        Gauge.builder("pizza_count", () -> 2).register(metrics);
    }

}
