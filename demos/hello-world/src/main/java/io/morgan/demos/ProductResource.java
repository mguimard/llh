package io.morgan.demos;

import java.util.List;

import javax.print.attribute.standard.Media;

import io.quarkus.logging.Log;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/products") 
public class ProductResource {
    // GET => liste de produits
    // GET /id => un produit
    // POST => créer nouveau produit
    // DELETE /id => supprimer
    // PUT/PATCH /id => modifier

    List<Product> products = List.of(
        new Product(1L, "radio-reveil", 10.0),
        new Product(2L, "grille-pain", 15.0)
    );

    @GET 
    @Produces (MediaType.TEXT_PLAIN)
    public List<Product> getAllProducts(){
        return products;
    }

    @GET
    @Produces (MediaType.TEXT_PLAIN)
    @Path("/{id}")
    public Product getProductById(@PathParam ("id") Long id){
        Log.info("productid = " + id);
        return products.stream().filter(p -> p.id()  == id)
        .findFirst().orElseThrow(NotFoundException::new);
    }

}
