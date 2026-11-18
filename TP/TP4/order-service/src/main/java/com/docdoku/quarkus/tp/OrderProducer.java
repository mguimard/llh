package com.docdoku.quarkus.tp;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderProducer {
    @Channel("orders")
    Emitter<Order> emitter;

    public void send(Order order) {
        var cstage = emitter.send(order);
        cstage.thenRun(() -> System.out.println("Sent to broker"));
    }

}