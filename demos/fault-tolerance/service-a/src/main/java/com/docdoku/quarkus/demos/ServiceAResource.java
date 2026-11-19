package com.docdoku.quarkus.demos;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.faulttolerance.Bulkhead;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.faulttolerance.exceptions.CircuitBreakerOpenException;
import org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.quarkus.logging.Log;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/hello")
public class ServiceAResource {

    @Inject
    @RestClient
    MyRemoteService serviceB;

    @ConfigProperty(name = "my.super.variable")
    String mySuperVariable;

    @GET
    @Path("/hello-world")
    public String hw() {
        return "hello world " + mySuperVariable;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @RunOnVirtualThread
    public String hello() {

        String response = callServiceBWithCircuitBreaker();
        // ProcessingException, service B est offline
        // TimeoutException si @Timeout
        // BulkheadException si le nombre d'appels concurents est supérieur à la value
        // (5)
        // CircuitBreakerOpenException

        return "Hello from service A " + response;
    }

    private AtomicInteger counter = new AtomicInteger(0);

    @CircuitBreaker(requestVolumeThreshold = 10, failureRatio = 0.5, failOn = ProcessingException.class)
    @Fallback(applyOn = CircuitBreakerOpenException.class, fallbackMethod = "fallbackCircuit")
    public String callServiceBWithCircuitBreaker() {
        Log.info("Trying....");
        return serviceB.hello();
    }

    public String fallbackCircuit() {
        return "circuit opened";
    }

    @Fallback(fallbackMethod = "fallbackNetworkIssue", applyOn = { ProcessingException.class })
    public String callServiceBWithFallback() {
        return serviceB.hello();
    }

    public String fallbackNetworkIssue() {
        return "service B is unreachable";
    }

    @Bulkhead(value = 5)
    public String callServiceBWithBulkhead() {
        Log.info("Bulkhead ...." + counter.getAndIncrement());
        return serviceB.hello(); // 10s
    }

    @Timeout(value = 1000)
    @Retry(maxRetries = -1, retryOn = { ProcessingException.class,
            TimeoutException.class }, delay = 1000, delayUnit = ChronoUnit.MILLIS)
    public String callServiceBWithTimeoutAndRetry() {
        Log.info("Retrying....");
        return serviceB.hello(); // 10s
    }

    @Timeout(value = 5000)
    public String callServiceBWithTimeout() {
        Log.info("Trying....");
        return serviceB.hello(); // 10s
    }

    @Retry(maxRetries = -1, retryOn = ProcessingException.class, delay = 1000, delayUnit = ChronoUnit.MILLIS)
    public String callServiceBWithRetry() {
        Log.info("Retrying....");
        return serviceB.hello();
    }
}
