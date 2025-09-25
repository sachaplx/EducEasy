# Titre du projet

Educ’Easy — Plateforme de gestion scolaire (direction & enseignants)

# Contexte / origine

Besoin d’un outil léger permettant aux directeurs de piloter plusieurs écoles (classes, enseignants) et aux instituteurs de gérer leurs élèves (notes, absences, remarques).

# Objectif

- Centraliser les données de scolarité (élèves, classes, écoles).
- Offrir des workflows simples : consultation, saisie des notes/absences/remarques, recherche.
- Garantir la sécurité (RBAC, JWT, validation).

# Public cible

- Directeur d’établissement
- Enseignants

# Fonctionnalités principales

- Auth (login/register), gestion des rôles, guard front.
- Tableau de bord Directeur : écoles → classes → élèves.
- Tableau de bord Institeur : classes du prof → élèves.
- Fiche élève avec onglets : Notes / Absences / Remarques (+ dialogues d’ajout).
- Recherche élève globale (autocomplétion).

# Contraintes techniques connues

- API REST Spring Boot 3 + JPA/Hibernate + MySQL.
- JWT stateless, BCrypt, Spring Security 6.
- Front Vite + Vue3 + Vuetify + Pinia + Axios (interceptors).
- RGPD de base (minimisation, rôles, aucune donnée sensible superflue).

# Données personnelles

Nom, prénom (élèves, profs, directeurs), e-mail/username (users). Stockage chiffré du mot de passe (BCrypt), JWT côté client.
