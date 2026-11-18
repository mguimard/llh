# TP4 : Mise en place d'une communication orientée message avec Kafka entre deux microservices

Le but de ce TP est de mettre en place une communication orientée objet entre deux micro-services, à l'aide d'un broker Kafka.

- order-service (8080): microservice qui expose une API REST pour déclencher une nouvelle commande
- cooking-service (8081): microservice qui attend de nouvelles commandes sur le broker.

```
       REST: HTTP POST /orders
Client ────────────────────────► order-service
                                    |
                                    |
                                    | publish
                                    v
                            +---------------+
                            |     Kafka     |
                            |    Broker     |
                            +-------+-------+
                                    |
                                    | consume
                                    v
                            cooking-service
                                    |
                                    v
                            Process order
```

Scénario :

1. Le client envoie une nouvelle commande à `order-service` (REST)
2. `order-service` valide la commande.
3. `order-service` publie un événement Kafka.
4. Kafka stocke le message dans un topic.
5. `cooking-service` reçoit le message.
6. `cooking-service` traite la commande (system out).

## Installation/configuration Kafka

Non, c'est une blague, les dev services de quarkus vont le faire pour nous !

## Créer les projets `order-service` et `cooking-service`

Créez les deux projets Quarkus.

Ajoutez l'extension Quarkus permettant d'utiliser Kafka avec Reactive Messaging :

```bash
./mvnw quarkus:add-extension -Dextensions="messaging-kafka"
```

Le projet doit notamment contenir :

```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-messaging-kafka</artifactId>
</dependency>
```

Pense à ajouter dans application.properties de cooking-service le port 8081

```ini
quarkus.http.port=8081
```

## Modèle d'une commande

Dans `order-service`, créez un record `Order`.

Il représentera une commande reçue par l'API REST.

```java
public record Order(Long id, String customer, String dish, int quantity) {
}
```

Une commande pourra par exemple être représentée ainsi :

```json
{
  "id": 1,
  "customer": "Alice",
  "dish": "Pizza",
  "quantity": 2
}
```

---

## Créer l'API REST de `order-service`

Le service doit exposer :

```
POST /orders
```

Le client pourra envoyer :

```json
curl -iXPOST http://localhost:8080/orders \
-H "Content-Type: application/json" \
-d '{
    "id": 1,
    "customer": "Alice",
    "dish": "Pizza",
    "quantity": 2
}'
```

Créez une ressource REST :

```java
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/orders")
public class OrderResource {

    @Inject
    OrderProducer orderProducer;

    @POST
    public Response createOrder(Order order) {
        orderProducer.send(order);
        return Response.accepted(order).build();
    }
}
```

Le code HTTP `202 Accepted` indique que la demande a été acceptée pour traitement.

Le traitement de la commande n'est pas effectué directement dans cette requête HTTP.

---

## Créer le producteur Kafka

Créez une classe `OrderProducer` et l'implémenter.

Elle sera responsable de publier les commandes dans Kafka.

```java
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class OrderProducer {
    // A coder
}
```

## Configuration de Kafka dans `order-service`

Dans :

```bash
src/main/resources/application.properties
```

ajoutez :

```ini
mp.messaging.outgoing.orders.connector=smallrye-kafka
mp.messaging.outgoing.orders.topic=orders
mp.messaging.outgoing.orders.value.serializer=io.quarkus.kafka.client.serialization.ObjectMapperSerializer
```

Le nom du canal est "orders".

Tester le webservice POST /orders, vérifier le statut de retour (202)

## Création du consumer

Le `cooking-service` doit maintenant écouter le topic Kafka "orders" :

Créez la classe OrderConsumer et l'implémenter.

```java

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class OrderConsumer {
    // A coder
}
```

Dans :

```bash
src/main/resources/application.properties
```

ajoutez :

```ini
mp.messaging.incoming.orders.connector=smallrye-kafka
mp.messaging.incoming.orders.topic=orders
```

## Tester la communication

Assurez-vous que les 2 microservices et kafka soient bien démarrés.

Lancer une requête POST /orders sur order-service.

Dans les logs du `cooking-service`, vous devez observer un message similaire à :

```txt
Cooking order 1 for customer Alice: 2 x Pizza
```

## Ajouter un identifiant de commande

Modifiez l'API afin que l'identifiant de commande soit généré par `order-service`.

Le client ne devra plus fournir :

```json
{
  "id": 1,
  "customer": "Alice",
  "dish": "Pizza",
  "quantity": 2
}
```

mais seulement :

```json
{
  "customer": "Alice",
  "dish": "Pizza",
  "quantity": 2
}
```

L'application devra générer l'identifiant.

Pour cet exercice, un compteur en mémoire est suffisant.

Exemple :

```java
private AtomicLong sequence = new AtomicLong();

public Long nextId() {
    return sequence.incrementAndGet();
}
```

## Expérimenter l'asynchronisme

Arrêtez `cooking-service`.

Envoyez plusieurs commandes POST /orders à `order-service`.

Redémarrez ensuite `cooking-service`.

Observez ce qui se passe.

## Bonus — Événement de fin de cuisson

Faites évoluer l'architecture afin que `cooking-service` publie à son tour un événement "OrderCooked" et que `order-service` réagisse avec un log lors d'une préparation/cuisson terminée
