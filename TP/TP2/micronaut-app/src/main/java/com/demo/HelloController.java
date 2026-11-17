package com.demo;

import io.micronaut.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import io.micronaut.context.annotation.Parameter;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.NotFoundException;

@Controller("/api")
public class HelloController {

    static List<Product> products = new ArrayList<>(List.of(
        new Product(1L, "chaussure", 12.0),
        new Product(2L, "K-way", 23.0)
    ));

    @Get("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from Micronaut";
    }

    @Get("/products")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Product> getAllProducts(){
        return products;
    }

    @Get("/products/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Product getProduct(@Parameter ("id") Long id){
        return products.stream().filter(p -> p.id() == id).findFirst().orElseThrow(NotFoundException::new);
    }

    @Post ("/products")    
    public HttpResponse<Product> createProduct(@Body Product p){
        Product newProduct = new Product(Long.valueOf(products.size() + 1), p.name(), p.price());
        products.add(newProduct);
        return HttpResponse.created(newProduct);
    }
}