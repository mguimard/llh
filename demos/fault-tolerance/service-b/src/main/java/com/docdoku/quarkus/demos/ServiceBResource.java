package com.docdoku.quarkus.demos;

import java.time.Duration;

import io.quarkus.logging.Log;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/hello")
public class ServiceBResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() throws InterruptedException {

        Thread.sleep(Duration.ofSeconds((int) (Math.random() * 10)));
        Log.info("Fin de traitement");
        return "Hello from service B";
    }
}
