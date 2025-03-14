# Gestion d’Hôpital

Une application web de gestion d’hôpital permettant de gérer les patients via une interface conviviale. Pour démarrer rapidement, le projet utilise **Spring Boot** et **Thymeleaf** pour le rendu des vues. Une migration vers **Angular** est prévue pour offrir une expérience utilisateur plus dynamique et réactive.

## Table des matières

- [Fonctionnalités](#fonctionnalités)
- [Technologies](#technologies)
- [Installation](#installation)
- [Utilisation](#utilisation)
- [Architecture du Projet](#architecture-du-projet)
- [Plan de Migration vers Angular](#plan-de-migration-vers-angular)
- [Contribution](#contribution)
- [Licence](#licence)

## Fonctionnalités

- **Gestion des Patients** : Création, lecture, mise à jour et suppression (CRUD) des patients.
- **Recherche et Pagination** : Recherche par nom et affichage paginé des résultats.
- **Validation** : Validation des données utilisateur grâce à Jakarta Validation.
- **Interface Utilisateur** : Interface développée avec Thymeleaf pour un rendu HTML dynamique.
- **Préparation API REST** : Mise en place progressive d'endpoints pour une future consommation par un front-end Angular.

## Technologies

- **Java 11+**
- **Spring Boot** – Pour le développement du backend.
- **Spring MVC** – Pour la gestion des requêtes HTTP.
- **Spring Data JPA** – Pour la persistance des données.
- **Jakarta Servlet & Validation** – Pour la gestion des requêtes et des validations.
- **Thymeleaf** – Pour le rendu des vues côté serveur (actuellement).
- **Angular** – (Futur) Pour une interface utilisateur plus riche et interactive.
- **Lombok** – Pour réduire le code boilerplate.

## Installation

### Prérequis

- JDK 11 ou supérieur
- Maven (ou Gradle)
- Un SGBD (par exemple, MySQL, PostgreSQL ou H2 pour les tests)

### Clonage du dépôt

```bash
git clone https://github.com/votre-nom-utilisateur/gestion-hopital.git
cd gestion-hopital
