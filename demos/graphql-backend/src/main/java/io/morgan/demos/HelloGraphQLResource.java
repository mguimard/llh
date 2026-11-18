package io.morgan.demos;

import java.util.List;

import org.eclipse.microprofile.graphql.DefaultValue;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;

import io.morgan.demos.HelloGraphQLResource.Pizza;

@GraphQLApi
public class HelloGraphQLResource {

    public record Pizza(String name, String category) {
    }

    public record User(String login, int age) {
    }

    static List<Pizza> pizzas = List.of(
            new Pizza("p1", "cat1"),
            new Pizza("p2", "cat1"),
            new Pizza("p3", "cat2"));

    static List<User> users = List.of(
            new User("u1", 20),
            new User("u2", 22));

    @Query("pizzas")
    @Description("Retreive pizzas")
    public List<Pizza> getPizzas(@DefaultValue("") String category) {
        if (category.isEmpty())
            return pizzas;
        else
            return pizzas.stream().filter(p -> p.category().equals(category))
                    .toList();
    }

    @Query("users")
    @Description("Retreive users")
    public List<User> getUsers() {
        return users;
    }
}