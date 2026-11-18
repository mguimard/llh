package com.docdoku.quarkus.demos;

import java.util.List;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;

@GraphQLApi
public class HelloResource {

    record Pizza(String name, String category) {
    }

    static List<Pizza> pizzas = List.of(
            new Pizza("p1", "cat1"),
            new Pizza("p2", "cat1"),
            new Pizza("p3", "cat2"));

    @Query("pizzas")
    public List<Pizza> getPizzas() {
        return pizzas;
    }

}
