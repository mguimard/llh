package com.demo;

import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path ("/api/products")
@Produces (MediaType.APPLICATION_JSON)
@Consumes (MediaType.APPLICATION_JSON)
public class ProductResource {
    
    static List<Product> products = new ArrayList<>(List.of(
        new Product(1L, "chaussure", 12.0),
        new Product(2L, "K-way", 23.0)
    ));

    @GET
    public List<Product> getProducts() {
        return products;
    }

    @POST
    public Response createProduct(Product product) {
        Product newProduct = new Product((long) (products.size() + 1), product.name(), product.price());
        products.add(newProduct);

        return Response.status(Response.Status.CREATED)
                .entity(newProduct)
                .build();
    }

    @GET
    @Path("/{id}")
    public Product getProduct(@PathParam("id") Long id) {
        return products.stream()
                .filter(p -> p.id().equals(id))
                .findFirst()
                .orElseThrow(() ->
                    new NotFoundException("Product not found"));
    }
}
