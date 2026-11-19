package com.docdoku.quarkus.demos;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

@Readiness
public class ReadyCheck2 implements HealthCheck {

    @Override
    public HealthCheckResponse call() {
        var status = Math.random() > 0.5;
        return HealthCheckResponse.builder().name("check numero 2").status(status).build();
    }

}
