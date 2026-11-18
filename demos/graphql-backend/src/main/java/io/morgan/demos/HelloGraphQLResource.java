package io.morgan.demos;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.microprofile.graphql.DefaultValue;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Name;
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

    static List<User> users = new ArrayList<>(List.of(
            new User("u1", 20),
            new User("u2", 22)));

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

    /*
     * mutation{
     * addUser(login: "alice", age: 24){
     * login
     * }
     * }
     */
    @Mutation
    public User addUser(@Name("login") String login, @Name("age") int age) {
        User u = new User(login, age);
        users.add(u);
        return u;
    }

    /*
     * mutation{
     * deleteUser(login: "alice")
     * }
     */
    @Mutation
    public boolean deleteUser(@Name("login") String login) {

        User user = users.stream().filter(u -> u.login().equals(login)).findFirst().orElse(null);

        if (user == null) {
            return false;
        }

        users.remove(user);
        return true;
    }
}