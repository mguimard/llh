# TP5 : Mise en pratique des 6 patterns de tolérance à la panne du standard MicroProfile

Le but de cet exercice est de découvrir les patterns de tolérance à la panne : `Retry`, `Timeout`, `Fallback` et `Circuit Breaker` avec SmallRye Fault Tolerance.

Nous reprenons les sources du TP précédent.

## Objectifs

Dans l'exercice précédent, `order-service` communiquait avec `product-service` au moyen d'une API REST.

Cette communication présente cependant un problème : **`order-service` dépend directement de la disponibilité de `product-service`**.

Que se passe-t-il si :

- `product-service` est temporairement indisponible ?
- le réseau est lent ?
- `product-service` met trop de temps à répondre ?
- les appels échouent plusieurs fois de suite ?

Dans cet exercice, vous allez rendre cette communication plus robuste à l'aide de **SmallRye Fault Tolerance** et les mécanismes suivants :

- `@Retry`
- `@Timeout`
- `@Fallback`
- `@CircuitBreaker`

À l'issue de l'exercice, vous devrez être capable de :

- identifier les problèmes liés aux appels synchrones entre microservices ;
- configurer des stratégies de résilience ;
- effectuer automatiquement de nouvelles tentatives ;
- limiter la durée d'un appel distant ;
- fournir une réponse de secours ;
- empêcher les appels vers un service défaillant ;
- comprendre le fonctionnement d'un circuit breaker.

## 1. Mise en place de SmallRye Fault Tolerance

Ajoutez l'extension Fault Tolerance au `pom.xml` du projet `order-service`.

```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-smallrye-fault-tolerance</artifactId>
</dependency>
```

## 2. Première protection : Retry

Modifiez la méthode `OrderService.getProduct` pour y ajouter l'annotation `@Retry`:

```java
import org.eclipse.microprofile.faulttolerance.Retry;

[...]

@Retry(maxRetries = 3)
public Product getProduct(Long productId) {
    return client.getProduct(productId);
}
```

L'appel initial est effectué normalement, en cas d'échec, Quarkus peut effectuer des tentatives supplémentaires.

Test d'une panne :

- Arrêtez temporairement `product-service`.
- Appelez /orders/1
- Observez le comportement de `order-service`.

Redémarrez ensuite `product-service` et répétez l'expérience.

Ajoutez un délai entre les tentatives :

```java
@Retry(maxRetries = 3, delay = 500)
```

# 3. Limiter la durée d'un appel avec Timeout

Imaginez que `product-service` soit disponible mais extrêmement lent.

Pour simuler une lenteur :

```java
try {
    // 4 seconds
    Thread.sleep(4000);
} catch (InterruptedException e) {
    e.printStackTrace();
}
```

Modifiez la méthode `OrderService.getProduct` pour y ajouter l'annotation `@Timeout`:

```java
import org.eclipse.microprofile.faulttolerance.Timeout;

@Timeout(2000)
public Product getProduct(Long productId) {
    return client.getProduct(productId);
}
```

L'appel est maintenant limité à 2 secondes.

Si `product-service` ne répond pas suffisamment rapidement, l'appel échoue.

## 4. Combiner Retry et Timeout

Il est possible de combiner plusieurs stratégies :

```java
@Retry(maxRetries = 3, delay = 500)
@Timeout(2000)
public Product getProduct(Long productId) {
    return productClient.getProduct(productId);
}
```

# 5. Fournir une réponse de secours avec Fallback

Supposons maintenant que `product-service` soit complètement indisponible.

Même après plusieurs tentatives, l'appel échoue.

Créez une méthode de fallback dans `OrderService.java` :

```java
public Product getProductFallback(Long productId) {
    return new Product(productId, "Unknown product", 0, "Unknown");
}
```

Modifiez la méthode `OrderService.getProduct` pour y ajouter l'annotation `@Fallback`:

```java
@Fallback(fallbackMethod = "getProductFallback")
public Product getProduct(Long productId) {
    return client.getProduct(productId);
}
```

Tester.

# 7. Combiner Retry, Timeout et Fallback

Vous pouvez maintenant combiner les mécanismes :

```java
@Retry(maxRetries = 3, delay = 500)
@Timeout(2000)
@Fallback(fallbackMethod = "getProductFallback")
```

- Arrêtez `product-service`.
- Appelez : /orders/1

Le `order-service` doit finalement utiliser le fallback.

S'assurer que la réponse contient bien le produit "unknown"

# 8. Circuit Breaker

Imaginez que `product-service` soit complètement hors service.

Modifiez la méthode `OrderService.getProduct` pour y ajouter l'annotation `@CircuitBreaker`:

```java
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;

@CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 5000)
public Product getProduct(Long productId) {
    return client.getProduct(productId);
}
```

Le circuit observe les appels effectués.

Avec :

```
requestVolumeThreshold = 4
```

le circuit commence son analyse après un certain nombre de requêtes.

Avec :

```
failureRatio = 0.5
```

le circuit peut s'ouvrir lorsque le taux d'échec atteint 50 %.

Avec :

```
delay = 5000
```

le circuit reste ouvert pendant une période donnée avant de permettre une nouvelle tentative.

---

# 11\. Comprendre les états du Circuit Breaker

Un circuit breaker possède trois états principaux :

```
            +-------------+
            |    CLOSED   |
            +------+------+
                |
            trop d'échecs
                |
                v
            +-------------+
            |     OPEN    |
            +------+------+
                |
            après délai
                |
                v
            +-------------+
            |   HALF-OPEN |
            +------+------+
                |
            test réussi ?
                /      \
            oui       non
            |          |
            v          v
        CLOSED       OPEN
```

### CLOSED

Le fonctionnement est normal.

Les appels sont transmis à `product-service`.

### OPEN

Le service distant est considéré comme défaillant.

Les appels sont rejetés immédiatement.

### HALF-OPEN

Après un certain délai, un appel de test est autorisé.

Si le service fonctionne à nouveau, le circuit repasse à `CLOSED`.

Sinon, il repasse à `OPEN`.

---

# 12\. Combiner toutes les stratégies

Construisez maintenant une méthode protégée par les quatre mécanismes :

```
@Retry(maxRetries = 3, delay = 500)
@Timeout(2000)
@CircuitBreaker(
requestVolumeThreshold = 4,
failureRatio = 0.5,
delay = 5000
)
@Fallback(fallbackMethod = "getProductFallback")
public Product getProduct(Long productId) {
return productClient.getProduct(productId);
}

public Product getProductFallback(Long productId) {
Product product = new Product();

product.id = productId;
product.name = "Unknown product";
product.price = 0;
product.category = "Unknown";

return product;
}
```

Vous disposez maintenant d'une chaîne de protection :

```
                order-service
                    |
                    v
            Circuit Breaker
                    |
                    v
                Retry
                    |
                    v
                Timeout
                    |
                    v
            product-service
                    |
                +-----+-----+
                |           |
            succès       échec
                |           |
                v           v
            réponse     retry
                            |
                            ...
                            |
                        échec définitif
                            |
                            v
                        Fallback
```

---

# 13\. Expérimentation complète

Effectuez plusieurs scénarios.

## Scénario A — Fonctionnement normal

Les deux services sont démarrés.

Effectuez :

```
GET /orders/1
```

### Résultat attendu

La réponse contient les informations réelles du produit.

---

## Scénario B — Service indisponible

Arrêtez `product-service`.

Effectuez plusieurs fois :

```
GET /orders/1
```

Observez :

- les retries ;
- le délai entre les appels ;
- le fallback ;
- l'évolution du circuit breaker.

---

## Scénario C — Service lent

Modifiez temporairement `product-service` afin de ralentir la réponse :

```
Thread.sleep(5000);
```

Appelez ensuite :

```
GET /orders/1
```

Observez l'effet du `@Timeout`.

### Questions

1. Au bout de combien de temps l'appel est-il considéré comme trop lent ?
2. Le retry est-il déclenché ?
3. Quel est le résultat final ?

---

## Scénario D — Retour du service

Après avoir arrêté `product-service`, redémarrez-le.

Effectuez plusieurs appels :

```
GET /orders/1
```

Observez le comportement du circuit breaker lorsqu'il détecte que le service fonctionne à nouveau.

---

# Questions

Répondez aux questions suivantes.

### Retry

1. Quel problème `@Retry` permet-il de résoudre ?
2. Pourquoi ne faut-il pas utiliser un nombre illimité de retries ?
3. Quelle différence entre un problème temporaire et une panne durable ?

### Timeout

4. Pourquoi un timeout est-il nécessaire lors d'un appel entre microservices ?
5. Que peut-il se passer si aucun timeout n'est configuré ?

### Fallback

6. Qu'est-ce qu'une réponse dégradée ?
7. Pourquoi un fallback ne doit-il pas masquer systématiquement les erreurs ?

### Circuit Breaker

8. Quel problème le circuit breaker permet-il de résoudre ?
9. Que signifie l'état `OPEN` ?
10. Quel est le rôle de l'état `HALF-OPEN` ?
