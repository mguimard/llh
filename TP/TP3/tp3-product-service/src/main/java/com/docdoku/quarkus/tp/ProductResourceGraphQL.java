package com.docdoku.quarkus.tp;

import java.util.List;

import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;

import jakarta.inject.Inject;

@GraphQLApi
public class ProductResourceGraphQL {
    @Inject
    ProductService productService;

    @Query("products")
    @Description("Get all products")
    public List<Product> products() {
        return productService.getAllProducts();
    }

    @Query("product")
    @Description("Get product by id")
    public Product product(@Name("id") Long id) {
        return productService.getById(id);
    }
}
