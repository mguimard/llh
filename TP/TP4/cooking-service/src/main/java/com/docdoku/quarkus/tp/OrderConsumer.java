package com.docdoku.quarkus.tp;

import java.util.concurrent.CompletionStage;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.reactive.messaging.kafka.api.IncomingKafkaRecordMetadata;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderConsumer {

    @Channel("order-cooked")
    Emitter<OrderCooked> emitter;

    @Incoming("orders")
    @RunOnVirtualThread
    public CompletionStage<Void> onNewOrder(Message<Order> message) throws InterruptedException {
        Order order = message.getPayload();

        int partition = message.getMetadata(IncomingKafkaRecordMetadata.class)
                .map(IncomingKafkaRecordMetadata::getPartition)
                .orElse(-1);

        System.out.println("Processing order : " + order.id() + " from partition : " + partition);

        // bonus
        long start_time = System.nanoTime();

        Thread.sleep((int) (Math.random() * 1000) + 500);
        long end_time = System.nanoTime();
        double ms = (end_time - start_time) / 1e6;

        // var cstate =
        emitter.send(new OrderCooked(order.id(), ms));
        // cstate.thenRun(() -> System.out.println("Notification sent"));

        return message.ack();
    }

}
