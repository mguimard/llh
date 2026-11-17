# TP2 – Découverte de trois frameworks pour les microservices

L'objectif de ce TP est de découvrir et de comparer trois frameworks Java orientées microservices :

1. Spring Boot
2. Quarkus
3. Micronaut

Les trois projets devront exposer les mêmes fonctionnalités afin de faciliter leur comparaison.

## Ressources documentaires

- [Documentation officielle Spring Boot](https://spring.io/projects/spring-boot)
- [Documentation officielle Quarkus](https://quarkus.io/guides)
- [Documentation officielle Micronaut](https://docs.micronaut.io)
- [Tutoriel Micronaut avec Maven et Java](https://guides.micronaut.io/creating-your-first-micronaut-app-maven-java)

## Partie 1 – Création d'un projet Spring Boot

La création peut se faire avec Spring Initializr :

- Site : [Spring Initializr](https://start.spring.io/)
- Project : Maven
- Language : Java
- Packaging : Jar
- Java : 25
- Dependencies : Spring Web

Il est également possible de télécharger le projet depuis un terminal :

```bash
curl https://start.spring.io/starter.zip \
  -d type=maven-project \
  -d language=java \
  -d bootVersion=4.0.0 \
  -d groupId=com.example \
  -d artifactId=spring-demo \
  -d name=spring-demo \
  -d packageName=com.example.demo \
  -d dependencies=web \
  -o spring-demo.zip

unzip spring-demo.zip -d spring-demo
cd spring-demo
```

### Création d'un contrôleur REST

Créer la classe `HelloController.java` :

```java
package com.example.demo;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello from Spring Boot";
    }
}
```

### Démarrage du projet

```bash
mvn spring-boot:run
```

L'application démarre par défaut sur le port 8080.

Tester l'API :

```bash
curl http://localhost:8080/api/hello
```

Résultat attendu :

```txt
Hello from Spring Boot
```

### Travail demandé

1. Créer le projet Spring Boot.
2. Identifier les principaux répertoires du projet.
3. Repérer le fichier `pom.xml`.
4. Créer le contrôleur REST.
5. Démarrer l'application.
6. Tester l'endpoint `/api/hello`.
7. Ajouter un endpoint `/api/version` qui retourne la version de votre application.

## Partie 2 – Création d'un projet Quarkus

Exécuter la commande suivante :

```bash
mvn io.quarkus.platform:quarkus-maven-plugin:3.40.1.1:create \
  -DprojectGroupId=com.example \
  -DprojectArtifactId=quarkus-demo \
  -Dextensions=rest \
  -DnoCode

cd quarkus-demo
```

Cette commande crée un projet Maven avec le support REST de Quarkus.

Ou bien utiliser https://code.quarkus.io/

### Création d'une ressource REST

Créer la classe `HelloResource.java` :

```java
package com.example;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/hello")
public class HelloResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from Quarkus";
    }
}
```

### Démarrage du projet

```bash
./mvnw quarkus:dev
```

Tester l'API :

```bash
curl http://localhost:8080/api/hello
```

Résultat attendu :

```txt
Hello from Quarkus
```

### Travail demandé

1. Créer le projet Quarkus.
2. Examiner le fichier `pom.xml`.
3. Identifier les dépendances spécifiques à Quarkus.
4. Créer la ressource REST.
5. Démarrer l'application en mode développement.
6. Tester l'endpoint `/api/hello`.
7. Modifier le message retourné et observer le comportement du mode développement.

## Partie 3 – Création d'un projet Micronaut

### Création du projet avec Maven

Micronaut propose un outil en ligne de commande appelé Micronaut CLI.

Commande de création :

```bash
mn create-app com.example.micronautdemo \
  --build=maven \
  --lang=java

cd micronautdemo
```

Une autre possibilité, générateur en ligne : [Micronaut Launch](https://launch.micronaut.io)

### Création d'un contrôleur REST

Créer la classe `HelloController.java` :

```java
package com.example;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;

@Controller("/api")
public class HelloController {

    @Get("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from Micronaut";
    }
}
```

### Démarrage du projet

```bash
./mvnw mn:run
```

Tester l'API :

```bash
curl http://localhost:8080/api/hello
```

Résultat attendu :

```txt
Hello from Micronaut
```

### Travail demandé

1. Créer le projet Micronaut.
2. Examiner la structure du projet.
3. Identifier les dépendances dans le fichier `pom.xml`.
4. Créer le contrôleur REST.
5. Démarrer l'application.
6. Tester l'endpoint `/api/hello`.
7. Ajouter un endpoint `/api/version`.
8. Comparer les annotations utilisées avec celles de Spring Boot et Quarkus.

## Partie 4 : développer la même API avec les trois frameworks

Vous devez implémenter exactement les mêmes fonctionnalités dans les trois projets.

Modèle de données : Product (Product.java)

```java
public record Product(Long id, String name, double price) {}
```

Fonctionnalités à développer :

- `GET /api/products` : récupérer tous les produits.
- `GET /api/products/{id}` : récupérer un produit.
- `POST /api/products` : ajouter un produit.

Pour simplifier l'exercice, les données seront stockées dans une liste en mémoire, sans base de données.

Contraintes :

- Respecter les mêmes URLs dans les trois projets.
- Retourner les mêmes données JSON.
- Utiliser les codes HTTP appropriés (`200`, `201`, `204`, `404`).
- Tester les endpoints avec Postman ou curl.
- Comparer les difficultés rencontrées dans chaque framework.
