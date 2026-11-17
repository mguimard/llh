package com.example.springboot_app;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    static List<Product> products = new ArrayList<>(List.of(
        new Product(1L, "chaussure", 12.0),
        new Product(2L, "K-way", 23.0)
    ));

    @GetMapping
    public List<Product> getAllProducts() {
        return products;
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(
            @RequestBody Product product) {

        Product newProduct = new Product((long) products.size() + 1, product.name(), product.price());
        products.add(newProduct);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(newProduct);
    }
}
