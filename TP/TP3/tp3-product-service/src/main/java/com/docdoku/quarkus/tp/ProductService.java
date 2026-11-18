package com.docdoku.quarkus.tp;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProductService {

    static List<Product> products = Arrays.asList(
            new Product(1L, "Laptop", 1200.00, "Electronics"),
            new Product(2L, "Smartphone", 800.00, "Electronics"),
            new Product(3L, "Keyboard", 80.00, "Accessories"),
            new Product(4L, "Mouse", 40.00, "Accessories"),
            new Product(5L, "Desk", 300.00, "Furniture"));

    public List<Product> getAllProducts() {
        return Collections.unmodifiableList(products);
    }

    public List<Product> getAllProducts(@Nonnull String category) {
        return products.stream().filter(p -> p.category().equals(category)).toList();
    }

    public Product getById(Long id) {
        return products.stream().filter(p -> p.id() == id).findFirst().orElseThrow(IllegalArgumentException::new);
    }
}
