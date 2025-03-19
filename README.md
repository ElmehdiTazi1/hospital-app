# Système de Gestion Hospitalière

Une application web complète pour la gestion des patients hospitaliers, construite avec Java Spring Boot et suivant une architecture modulaire.

## Structure du Projet

Ce projet est organisé en modules Maven indépendants :

- **hospital-domain** : Entités et objets du domaine métier
- **hospital-repository** : Couche d'accès aux données
- **hospital-service** : Logique métier et services
- **hospital-web** : Interface utilisateur et contrôleurs
- **hospital-config** : Configuration partagée (logging, Java doc...)

## Fonctionnalités

- Gestion complète des patients (ajout, modification, suppression)
- Recherche de patients par nom
- Pagination des résultats
- Interface utilisateur responsive avec Bootstrap
- Validation des formulaires
- Gestion des erreurs

## Technologies Utilisées

- **Java 17**
- **Spring Boot 3.2.4**
- **Spring Data JPA**
- **Thymeleaf** avec Layout Dialect
- **Bootstrap 5.2.3**
- **Bootstrap Icons 1.10.3**
- **H2 Database** (en mémoire)
- **Lombok**
- **SLF4J/Logback** pour la journalisation
- **Maven** pour la gestion des dépendances

## Prérequis

- JDK 17+
- Maven 3.8+
- Git

## Installation et Démarrage

1. Clonez le dépôt :
   ```bash
   git clone https://github.com/votre-nom/hospital-app.git
   cd hospital-app