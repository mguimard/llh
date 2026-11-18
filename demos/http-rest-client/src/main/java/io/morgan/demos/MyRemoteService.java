package io.morgan.demos;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import java.util.List;

@RegisterRestClient(baseUri = "http://localhost:8080")
public interface MyRemoteService {

    @GET
    @Path("/hello/pizzas")
    List<Pizza> getPizzas(@QueryParam("cat") String category);

    static record Pizza(String name, String category) {
    }

}
