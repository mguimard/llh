package com.docdoku.quarkus.demos;

import org.eclipse.microprofile.lra.annotation.ws.rs.LRA;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/shipping")
@RegisterRestClient(configKey = "shipping-service")
public interface ShippingServiceClient {

    @POST
    public String startShippingProcess(@HeaderParam(LRA.LRA_HTTP_CONTEXT_HEADER) String lraId);
}
