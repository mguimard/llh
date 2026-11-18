package com.demo;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/orders")
public class OrderResource {

    public static record Order(Long id, int qty, String product) {
    }

    @Inject
    @Channel("orders-out")
    Emitter<Order> ordersChannel;

    @POST
    public Response hello() {
        var order = new Order(1L, 10, "grille pain");

        ordersChannel.send(order);

        return Response.accepted().build();
    }

}
