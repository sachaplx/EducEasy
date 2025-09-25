# Educ'Easy

**Educ'Easy** est une application web de gestion d’écoles, de classes et d’élèves.  
Elle propose une interface simple pour les **Directeurs** (principals) et les **Instituteurs** (teachers) afin de centraliser les informations des établissements, suivre les classes et consulter le dossier des élèves (notes, absences, remarques).

## Objectifs

- **Centraliser** la gestion des écoles, classes et élèves dans une seule application.
- **Sécuriser les accès** via une authentification JWT et des rôles (PRINCIPAL / TEACHER).
- **Simplifier le suivi pédagogique** : consultation et ajout des **notes**, **absences** et **remarques** par élève.
- **Respecter le périmètre** de chaque rôle :
  - **Directeur** : gère ses écoles, classes et élèves.
  - **Instituteur** : gère uniquement les classes dont il est maître et les élèves associés.
- **Offrir une base scalable** (Spring Boot + Hibernate + Vue 3) pour ajouter facilement d’autres rôles et fonctionnalités.

## Périmètre fonctionnel (MVP)

- **Auth**
  - Login (identifiant = _email ou username_) + mot de passe (BCrypt)
  - Register (PRINCIPAL / TEACHER)
- **Écoles & Classes**
  - Directeur : lister _ses_ écoles, voir les classes par école.
  - Professeur : lister _ses_ classes.
- **Élèves**
  - Recherche d’élèves
  - Fiche élève : **notes**, **absences**, **remarques** (consultation + ajout)
- **RBAC**
  - Vérification des droits par rôle et périmètre (inscriptions actives / écoles du directeur)

## Stack

- **Back-end** : Java 17, Spring Boot (Web, Security, JPA/Hibernate), JWT
- **Base de données** : MySQL
- **Front-end** : Vite + **Vue 3**, **Vuetify**, Axios, Pinia
- **Build/Run** : Maven (back), Node/Yarn/NPM (front)

## Structure (haute-niveau)

- `educeasy-core/` : API REST, services métier, sécurité, entités/DTO, repositories
- `educeasy-ui/` : application Vue 3 (login/register, dashboard, fiche élève)

## Installation rapide

### Prérequis

- **Java 17**, **Maven 3.9+**
- **Node 18+** (ou 20+), **npm** (ou **yarn**)
- **MySQL 8+**
