package com.docdoku.quarkus.tp;

import java.util.Arrays;
import java.util.List;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class OrderService {
    static List<Order> orders = Arrays.asList(
            new Order(1L, 1L, 2),
            new Order(2L, 3L, 5),
            new Order(3L, 2L, 1));

    @Inject
    @RestClient
    ProductResourceClient client;

    public Order getById(Long id) {
        return orders.stream()
                .filter(order -> order.id().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Product getProduct(Long productId) {
        return client.getProduct(productId);
    }
}
