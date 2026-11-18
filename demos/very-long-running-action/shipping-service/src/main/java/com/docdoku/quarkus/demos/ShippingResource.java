package com.docdoku.quarkus.demos;

import java.net.URI;

import org.eclipse.microprofile.lra.annotation.Compensate;
import org.eclipse.microprofile.lra.annotation.Complete;
import org.eclipse.microprofile.lra.annotation.ws.rs.LRA;
import org.eclipse.microprofile.lra.annotation.ws.rs.LRA.Type;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.smallrye.faulttolerance.api.AsynchronousNonBlocking;
import io.vertx.core.Future;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/shipping")
@ApplicationScoped
public class ShippingResource {

    URI lraUri;

    @Inject
    @RestClient
    MyHttpClient client;

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @LRA(value = Type.MANDATORY, end = false)
    public String process(@HeaderParam(LRA.LRA_HTTP_CONTEXT_HEADER) URI lraId) {
        System.out.println("Shipping team on it! 🫡");

        // On mémorise les URI pour plus tard
        this.lraUri = lraId;

        // asynchrone, peut prendre quelques jours....
        doWork();

        return "Accepted, will try our best to ship the product.";
    }

    @AsynchronousNonBlocking
    Future<Void> doWork() {
        try {
            Thread.sleep(2000);

            // une chance sur deux de prendre le transpalette dans la face
            if (Math.random() > 0.5) {
                System.out.println("Oh no, something went wrong! 🚫");
                client.put(lraUri.toString() + "/cancel");
            } else {
                // tout va bien, completion
                System.out.println("Product has been sent! ✅");
                client.put(lraUri.toString() + "/close");
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    @PUT
    @Compensate
    @Path("/compensate")
    public Response cancelShipping(@HeaderParam(LRA.LRA_HTTP_CONTEXT_HEADER) URI lraId) {
        System.out.println("You stupid employes! 😡 (compensate)");
        return Response.ok().build();
    }

    @PUT
    @Complete
    @Path("/complete")
    public Response validateShipping(@HeaderParam(LRA.LRA_HTTP_CONTEXT_HEADER) URI lraId) {
        System.out.println("Congratulations team! 😊 (complete)");
        return Response.ok().build();
    }

}
