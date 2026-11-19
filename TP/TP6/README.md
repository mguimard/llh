# TP6 : Construction des images OCI (Docker) classique (OpenJDK) et AOT (GraalVM) pour un déploiement sur un orchestrateur cloud

Le but de ce TP est de préparer des applications pour un **déploiement conteneurisé sur un orchestrateur cloud**, ainsi que de comparer deux types d'images OCI :

- une image classique exécutant l'application Quarkus sur une JVM OpenJDK
- une image native construite avec GraalVM, selon une approche AOT (_Ahead-Of-Time_)

## Windows !

Prérequis Windows pour builder des images natives

- Installer GraalVM https://www.graalvm.org/latest/getting-started/windows/

Télécharger, dézipper, et définir en environement (powershell)

```
$env:PATH += ";C:\Users\orsys\Downloads\graalvm-jdk-25.0.4+7.1\bin"
```

- Installer Visual Studio and Microsoft Visual C++ (MSVC). https://aka.ms/vs/17/release/vs_buildtools.exe

Lancer l'installation avant la pause.

## Architecture de départ

Nous reprenons les deux microservices du TP précédent.

- `order-service` reçoit les commandes en REST
- `cooking-service` consomme les `Order` depuis Kafka et publie un `OrderCooked` dans Kafka.

Le code a été modifié pour prendre en compte des problématiques de sérialisation/désérialisation des messages en mode natif.

Récupérer le code fournit, étudier les classes de désérialisation.

L'architecture cible est :

```
  +--------------------+
  |  Cloud / Cluster   |
  |                    |
  | +----------------+ |
  | | order-service  | | Conteneur
  | +----------------+ |
  |                    |
  | +----------------+ |
  | |cooking-service | | Conteneur
  | +----------------+ |
  +--------------------+
          |
          v
        Kafka
```

Modification ! Rendre la configuration possible en environnement :

```ini
# application.properties
kafka.bootstrap.servers=${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}
```

Ajouter l'extension pour gérer les builds docker

```bash
./mvnw quarkus:add-extension -Dextensions='quarkus-container-image-docker'
```

## Construire l'application en mode JVM

Dans les deux projets, construire les images JVM :

Pour `order-service` :

```bash
# linux
./mvnw package \
  -Dquarkus.container-image.build=true \
  -Dquarkus.container-image.group=tp \
  -Dquarkus.container-image.name=order-service \
  -Dquarkus.container-image.tag=jvm

# windows
./mvnw package --define quarkus.container-image.build=true --define quarkus.container-image.group=tp --define quarkus.container-image.name=order-service --define quarkus.container-image.tag=jvm
```

Pour `cooking-service` :

```bash
# linux
./mvnw package \
  -Dquarkus.container-image.build=true \
  -Dquarkus.container-image.group=tp \
  -Dquarkus.container-image.name=cooking-service \
  -Dquarkus.container-image.tag=jvm

# windows
./mvnw package --define quarkus.container-image.build=true --define quarkus.container-image.group=tp --define quarkus.container-image.name=cooking-service --define quarkus.container-image.tag=jvm
```

Vérifiez leur présence dans votre installation docker :

```bash
docker images
```

Comparez notamment :

- la taille des images
- leur nom
- leur tag
- leur date de création.

Vous pouvez également inspecter une image :

```bash
docker inspect tp/order-service:jvm
docker inspect tp/cooking-service:jvm
```

## Exécuter les images JVM

Ouvrir le fichier compose.yml et adapter si besoin de le nom des images.

Lancer une requête de création de commande, et vérifier les logs des services.

```bash
curl -iXPOST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{ "id": 1, "customer": "Alice", "dish": "Pizza", "quantity": 2}'
```

## Version image native

Quarkus permet également de compiler l'application en exécutable natif.

Construisez `order-service` en mode natif :

Ces commandes peuvent prendre un certain temps selon la machine.. (compilation !)

```bash
# linux
./mvnw package \
  -Dnative \
  -Dquarkus.container-image.build=true \
  -Dquarkus.container-image.group=tp \
  -Dquarkus.container-image.name=order-service \
  -Dquarkus.container-image.tag=native

# windows
./mvnw package --define native --define quarkus.container-image.build=true --define quarkus.container-image.group=tp --define quarkus.container-image.name=order-service --define quarkus.native.container-build=true --define quarkus.container-image.tag=native
```

Construisez `cooking-service` en mode natif :

```bash
# linux
./mvnw package \
  -Dnative \
  -Dquarkus.container-image.build=true \
  -Dquarkus.container-image.group=tp \
  -Dquarkus.container-image.name=cooking-service \
  -Dquarkus.container-image.tag=native

# windows
./mvnw package --define native --define quarkus.container-image.build=true --define quarkus.container-image.group=tp --define quarkus.container-image.name=cooking-service --define quarkus.native.container-build=true --define quarkus.container-image.tag=native
```

## Comparer les deux images

Vous disposez maintenant de :

```
tp/order-service:jvm
tp/order-service:native
tp/cooking-service:jvm
tp/cooking-service:native
```

Comparez leurs tailles avec la commande `docker images`

## Lancement avec docker compose

Dans le dossier qui contient compose.yml, lancer :

```bash
docker compose -f compose.jvm.yml up
```

Pour lancer la version native :

```bash
docker compose -f compose.native.yml up
```

Avec la commande `docker stats`, relevez les empreintes mémoire et comparez les valeurs entre la version jvm et native.
