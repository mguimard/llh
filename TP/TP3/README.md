# TP3 : Créations de services Restful et GraphQL, interrogation depuis un deuxième microservice Quarkus

Le but de ce TP est de mettre en pratique la communication entre microservices avec Quarkus.

## Présentation du TP

Vous allez développer deux microservices :

- un premier microservice product-service, responsable de l'exposition des données produits ;
- un second microservice order-service, chargé d'interroger product-service.

Le premier microservice exposera les données selon deux approches :

- une API RESTful
- une API GraphQL

Le second microservice devra consommer l'API REST du premier microservice.

## 1. Architecture de l'application

Vous allez construire l'architecture suivante :

```
                   curl
                    |
                    +
                    |
                HTTP / REST
                    |
                    v
        +----------------------+
        |    order-service     |
        |       Quarkus        |
        +----------+-----------+
                    |
                    +
                    |
                HTTP / REST
                    |
                    v
+--------------------------------------+
|          product-service             |
|               Quarkus                |
|                                      |
|       REST API        GraphQL API    |
+--------------------------------------+
```

Responsabilités des services :

- Le microservice **product-service** sera responsable des produits.
- Le microservice **order-service** simulera un service consommateur ayant besoin d'informations sur les produits.

## 2. Création du product-service

Créez un nouveau projet Quarkus nommé `tp3-product-service`

Ajouter la dépendance quarkus-rest-jackson au pom.xml (ou par la palette VScode)

```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-rest-jackson</artifactId>
</dependency>
```

Pour simplifier l'exercice, les produits pourront dans un premier temps être stockés directement en mémoire.

Créer un record `Product.java`

```java
public record Product(
        Long id,
        String name,
        double price,
        String category) {
}
```

Créer une classe `ProductService.java`, qui garde en mémoire la liste des produits

```java
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProductService {

    static List<Product> products = Arrays.asList(
        new Product(1L, "Laptop", 1200.00, "Electronics"),
        new Product(2L, "Smartphone", 800.00, "Electronics"),
        new Product(3L, "Keyboard", 80.00, "Accessories"),
        new Product(4L, "Mouse", 40.00, "Accessories"),
        new Product(5L, "Desk", 300.00, "Furniture")
    );

    public List<Product> getAllProducts(){
        return Collections.unmodifiableList(products);
    }

    public Product getById(Long id) {
        return products.stream().filter(p -> p.id() == id).findFirst().orElseThrow(IllegalArgumentException::new);
    }
}
```

## 3. Création du service REST

Créez une ressource REST permettant d'accéder aux produits `ProductResource.java`

```java
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;

@Path("/products")
public class ProductResource {

    @Inject
    ProductService productService;

    // code à écrire ....
}
```

L'API devra proposer au minimum les endpoints suivants :

```
GET /products
GET /products/{id}
```

- L'appel `GET /products` devra retourner l'ensemble des produits.
- L'appel `GET /products/1` devra retourner le produit correspondant à l'identifiant 1 (code 404 si non trouvé).

Exemple de réponse JSON :

```json
{
  "id": 1,
  "name": "Laptop",
  "price": 1200.0,
  "category": "Electronics"
}
```

- Créer la ressource REST.
- Implémenter l'accès à la liste des produits.
- Implémenter l'accès à un produit par son identifiant.
- Gérer le cas où le produit demandé n'existe pas.
- Tester les endpoints avec un client HTTP.

## 4. Ajout de fonctionnalités REST

Faites évoluer l'API afin de permettre la recherche de produits par catégorie.

Ajoutez l'endpoint :

```
GET /products?category=Electronics
```

Le résultat devra contenir uniquement les produits appartenant à la catégorie demandée.

## 5. Création de l'API GraphQL

L'objectif est d'exposer les mêmes données produits, mais avec GraphQL.

Ajouter la dépendance quarkus-smallrye-graphql dans pom.xml (ou depuis la palette VSCode):

```xml
<!-- GraphQL Support -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-smallrye-graphql</artifactId>
</dependency>
```

Créer une nouvelle resource `ProductResourceGraphQL.java` :

```java
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

    //@Query("product")
    // A coder plus tard...

}
```

Accéder au schéma : http://localhost:8080/graphql/schema.graphql

L'interface GraphQL est accessible sur http://localhost:8080/q/graphql-ui/

Créez un endpoint GraphQL permettant d'interroger la liste des produits.

Tester cette requete dans l'interface graphique :

```
query {
    products {
        id
        name
        price
        category
    }
}
```

Ajoutez également une requête permettant de récupérer un produit par son identifiant :

```
query {
    product(id: 1) {
        id
        name
        price
        category
    }
}
```

Implémenter la requête product.

## 6. Création du deuxième microservice

Créez un deuxième projet Quarkus nommé `tp3-order-service`

Ce microservice représentera un service de gestion de commandes.

Il devra être capable d'obtenir les informations d'un produit depuis `tp3-product-service`.

Créer le fichier `Order.java`

```java
public record Order(Long id, Long productId, int quantity) {
}
```

Créer le fichier `OrderService.java`. Les commandes seront en mémoire pour simplifier le code :

```java
import java.util.Arrays;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderService {
    static List<Order> orders = Arrays.asList(
            new Order(1L, 1L, 2),
            new Order(2L, 3L, 5),
            new Order(3L, 2L, 1));

    // A coder plus tard...
}
```

## 8. Appel REST depuis le deuxième microservice

Le order-service doit maintenant appeler l'API REST du product-service. Nous allons créer la classe qui permet de dialoguer avec.

Ajouter quarkus-rest-client-jackson et quarkus-rest-jackson au `pom.xml` (ou depuis la palette VSCode)

```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-rest-client-jackson</artifactId>
</dependency>
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-rest-jackson</artifactId>
</dependency>
```

Ajouter l'url du premier projet dans `application.properties` :

```ini
# Le port 8080 est déjà utilisé par le premier projet
quarkus.http.port=8081

# Adresse du premier projet
quarkus.rest-client.product-service.url=http://localhost:8080
```

Créer le client rest `ProductResourceClient.java` :

```java
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/products")
@RegisterRestClient(configKey = "product-service")
public interface ProductResourceClient {
    @GET
    @Path("/{id}")
    Product getProduct(@PathParam("id") Long id);
}
```

Copier le record `Product.java` dans le projet order-service.

Créer maintenant la ressource `OrderResource.java` et y créer le endpoint REST :

```
GET /orders/{id}
```

Lorsqu'une commande est demandée, le service devra :

- récupérer la commande
- récupérer le productId
- interroger product-service
- récupérer les informations du produit
- construire la réponse

Par exemple :

```json
{
  "id": 1,
  "product": {
    "id": 1,
    "name": "Laptop",
    "price": 1200.0,
    "category": "Electronics"
  },
  "quantity": 2
}
```

Exemple de record pour cette réponse :

```java
public record OrderResponse(
        Long id,
        Product product,
        int quantity) {
}
```

Tester l'appel à `/orders/1`. Puis répondre aux questions suivantes :

- Que se passe-t-il lorsque product-service est indisponible ?
- Quelle erreur est remontée par order-service ?

## Bonus

Faire communiquer les services avec l'API GraphQL.
