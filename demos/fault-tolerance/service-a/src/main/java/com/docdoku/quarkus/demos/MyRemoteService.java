package com.docdoku.quarkus.demos;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@RegisterRestClient(baseUri = "http://localhost:8081")
public interface MyRemoteService {

    @GET
    @Path("/hello")
    String hello();

}
