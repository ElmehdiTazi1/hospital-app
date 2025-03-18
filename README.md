# Application de Gestion Hospitalière

Ce projet est une application de gestion hospitalière développée avec Spring Boot et organisée en architecture multimodule.

## Structure du Projet

L'application est divisée en plusieurs modules :

- **hospital-domain** : Contient les entités JPA et les objets du domaine métier
- **hospital-repository** : Gère l'accès aux données et les repositories Spring Data JPA
- **hospital-service** : Contient la logique métier et les services
- **hospital-web** : Interface utilisateur avec Spring MVC et Thymeleaf

## Prérequis

- Java 17
- Maven 3.6+
- Base de données (H2 en mémoire par défaut)

