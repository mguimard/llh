package com.docdoku.quarkus.tp;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderCookedConsumer {

    @Incoming("order-cooked")
    public void onOrderCooked(OrderCooked orderCooked) {
        System.out.println("Order " + orderCooked.id() + " is ready (cooked in " + orderCooked.ms() + "ms)");
    }

}
