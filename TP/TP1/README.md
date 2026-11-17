# TP1 : Setup de l'environnement, création d'un premier microservice en Java

Le but de ce TP est de préparer l'environnement (outillage, IDE) et de générer un preomier projet.

## Prérequis

- VSCode https://code.visualstudio.com/thank-you?dv=win&build=stable
- Maven
- Java 25 https://adoptium.net/download?link=https%3A%2F%2Fgithub.com%2Fadoptium%2Ftemurin25-binaries%2Freleases%2Fdownload%2Fjdk-25.0.5%252B8%2FOpenJDK25U-jdk_x64_linux_hotspot_25.0.5_8.tar.gz&vendor=Adoptium&checksum=68982529869257d0ee6b824ec5aa270ff8b118bb438670da0b97ebdab178d7fb
- Docker (déja installé)

Dans VSCode installer les extensions suivantes :

- Quarkus (par Red Hat)
- Language support for Java (par Red Hat)

Dans un terminal, vérifier les versions de Docker, Maven et Java

```bash
docker --version
mvn --version
java -version
```

## Création d'un premier microserice

Ouvrir un terminal et lancer la commande de création du projet.

```bash
mvn io.quarkus:quarkus-maven-plugin:3.40.1.1:create \
    -DprojectGroupId=llh.training \
    -DprojectArtifactId=my-first-microservice \
    -DclassName="llh.training.HelloResource" \
    -Dpath="/my-first-microservice"
```

Cela va créer un répertoire "my-first-microservice", se placer dans ce dossier et exécuter la commande de démarrage

```bash
cd my-first-microservice
./mvnw quarkus:dev
```

Accéder à http://localhost:8080, et découvrir les différents écrans et outils à disposition.

Inspecter la classe "HelloResource.java", et modifier le message pour la requete GET.

Appeler la requête depuis un navigateur et vérifier que le résultat est à jour.
