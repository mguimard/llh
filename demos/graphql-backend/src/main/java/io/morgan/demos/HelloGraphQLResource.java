package io.morgan.demos;

import java.util.List;

import org.eclipse.microprofile.graphql.DefaultValue;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;

@GraphQLApi
public class HelloGraphQLResource {

    static List<Pizza> pizzas = List.of(
            new Pizza("p1", "cat1"),
            new Pizza("p2", "cat1"),
            new Pizza("p3", "cat2"));

    @Query("pizzas")
    @Description("Retreive pizzas")
    public List<Pizza> getPizzas(@DefaultValue("") String category) {
        return pizzas;
    }
}