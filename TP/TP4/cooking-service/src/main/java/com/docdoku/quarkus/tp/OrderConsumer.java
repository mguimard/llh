package com.docdoku.quarkus.tp;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderConsumer {

    @Channel("order-cooked")
    Emitter<OrderCooked> emitter;

    @Incoming("orders")
    public void onNewOrder(Order order) throws InterruptedException {
        System.out.println("Processing order : " + order);

        // bonus
        long start_time = System.nanoTime();

        Thread.sleep(1000);
        long end_time = System.nanoTime();
        double ms = (end_time - start_time) / 1e6;

        var cstate = emitter.send(new OrderCooked(order.id(), ms));
        cstate.thenRun(() -> System.out.println("Notification sent"));
    }

}
