package com.docdoku.quarkus.tp;

import java.util.concurrent.atomic.AtomicLong;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/orders")
public class OrderResource {

    @Inject
    OrderProducer producer;

    private AtomicLong sequence = new AtomicLong();

    public Long nextId() {
        return sequence.incrementAndGet();
    }

    @POST
    public Response newOrder(Order order) {
        System.out.println(order);
        producer.send(new Order(nextId(), order.customer(), order.dish(), order.quantity()));
        return Response.accepted().build();
    }
}
