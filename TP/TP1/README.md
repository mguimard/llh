# TP1 : Setup de l'environnement, création d'un premier microservice en Java

Le but de ce TP est de préparer l'environnement (outillage, IDE) et de générer un preomier projet.

## Prérequis

- VSCode https://code.visualstudio.com/thank-you?dv=win&build=stable
- Maven
- Java 25
- Docker (déja installé)

Installer Maven :

- Lancer powershell en tant qu'admin

```powershell
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

choco install microsoft-openjdk25 maven
```

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
