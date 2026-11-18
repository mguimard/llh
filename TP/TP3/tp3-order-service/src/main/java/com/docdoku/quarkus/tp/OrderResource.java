package com.docdoku.quarkus.tp;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/orders")
public class OrderResource {

    @Inject
    OrderService orderService;

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response order(Long id) {
        Order order = orderService.getById(id);

        if (order == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Product product = orderService.getProduct(order.productId());

        if (product == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        OrderResponse response = new OrderResponse(
                order.id(),
                product,
                order.quantity());

        return Response.ok(response).build();
    }
}
