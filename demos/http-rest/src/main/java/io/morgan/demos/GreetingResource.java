package io.morgan.demos;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.quarkus.logging.Log;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

record Pizza(String name, String category) {
};

@Path("/hello")
public class GreetingResource {

    List<Pizza> pizzas = new ArrayList<>(List.of(
            new Pizza("saumon", "tomate"),
            new Pizza("savoyarde", "creme")));

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from Quarkus REST";
    }

    @GET
    @Path("/pizzas")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Pizza> getAllPizzas(@QueryParam("cat") String category) {
        if (category == null || category.isEmpty())
            return pizzas;

        return pizzas.stream().filter(p -> p.category().equals(category))
                .toList();
    }

    @POST
    @Path("/pizzas")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createPizza(Pizza p) throws URISyntaxException {
        Log.info("Pizza:  " + p);

        return Response.created(new URI("/hello/pizza/42")).build();
    }

}
