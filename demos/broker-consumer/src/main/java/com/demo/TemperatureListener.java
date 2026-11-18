package com.demo;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TemperatureListener {

    @Incoming("temp-in")
    public void onMessage(String temperature) {
        Log.info("Nouvelle température: " + temperature);
    }

}