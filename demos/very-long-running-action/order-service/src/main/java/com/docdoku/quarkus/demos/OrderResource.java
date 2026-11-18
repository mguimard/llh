package com.docdoku.quarkus.demos;

import java.net.URI;

import org.eclipse.microprofile.lra.annotation.Compensate;
import org.eclipse.microprofile.lra.annotation.Complete;
import org.eclipse.microprofile.lra.annotation.ws.rs.LRA;
import org.eclipse.microprofile.lra.annotation.ws.rs.LRA.Type;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import jakarta.inject.Inject;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/orders")
public class OrderResource {

    @Inject
    @RestClient
    ShippingServiceClient client;

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @LRA(value = Type.REQUIRED, end = false)
    public String createOrder(@HeaderParam(LRA.LRA_HTTP_CONTEXT_HEADER) URI lraId) {
        System.out.println("Oh, someone ordered a product! 👍");
        String shippingMessage = client.startShippingProcess(lraId.toString());
        return "Thank you for your order! Message from shipping service: " + shippingMessage;
    }

    @PUT
    @Compensate
    @Path("/compensate")
    public Response cancelOrder(@HeaderParam(LRA.LRA_HTTP_CONTEXT_HEADER) URI lraId) {
        System.out.println("Sorry dear customer, cancelling order! 💥 (compenste)");
        return Response.ok().build();
    }

    @PUT
    @Complete
    @Path("/complete")
    public Response validateOrder(@HeaderParam(LRA.LRA_HTTP_CONTEXT_HEADER) URI lraId) {
        System.out.println("Dear customer, your order has been shipped! ❤️ (complete)");
        return Response.ok().build();
    }
}
