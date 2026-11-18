package com.docdoku.quarkus.tp;

import java.util.List;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

// Rest API
@Path("/products")
public class ProductResource {

    @Inject
    ProductService productService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Product> products(@QueryParam("category") String category) {
        if (category != null && !category.isEmpty()) {
            return productService.getAllProducts(category);
        }

        return productService.getAllProducts();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response product(Long id) {
        try {
            return Response.ok(productService.getById(id)).build();
        } catch (Exception e) {
            return Response.status(404).build();
        }
    }
}
