package com.demo;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrdersListener {

    public static record Order(Long id, int qty, String name) {
    }

    @Incoming("orders-in")
    public void onMessage(Order o) {
        Log.info("Nouvelle commande: " + o);

        // traitement... errors ?
        if (Math.random() > 0.5) {
            // OK
            // publie order ok
        } else {
            // publie order-errors
        }
    }

}