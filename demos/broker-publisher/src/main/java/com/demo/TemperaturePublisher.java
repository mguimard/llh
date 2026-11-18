package com.demo;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TemperaturePublisher {

    @Inject
    @Channel("temp-out")
    Emitter<String> topic;

    @Scheduled(every = "2s")
    public void sendTemp() {
        Log.info("Send temp");
        try {
            topic.send("" + ((int) (Math.random() * 1000))).toCompletableFuture().get();
            Log.info("Sent");
        } catch (Exception e) {
            Log.info("Error: " + e.getMessage());
        }

    }

}