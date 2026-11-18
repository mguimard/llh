package com.docdoku.quarkus.demos;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.quarkus.rest.client.reactive.Url;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.core.Response;

@RegisterRestClient(baseUri = "http://localhost:8080")
public interface MyHttpClient {
    @PUT
    Response put(@Url String url);
}
