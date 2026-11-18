package com.docdoku.quarkus.tp;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/products")
@RegisterRestClient(configKey = "product-service")
public interface ProductResourceClient {
    @GET
    @Path("/{id}")
    Product getProduct(@PathParam("id") Long id);
}
