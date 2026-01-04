# Educ’Easy — Documentation d’intégration (UI ↔ Métier)

**Auteur :** Nom Prénom — **Date :** 19/09/2025

## 1) Ce qui a été relié

- **Login (Vue)** → `POST /auth/login` avec `{ identifier, password }` → stockage JWT (`localStorage`) + header `Authorization` via Axios → redirection Accueil.
- **Register (Vue)** → `POST /auth/register` avec `{ email, username, password, confirmPassword, firstName, lastName, role }` → message succès puis login.
- **Dashboard Principal** → `GET /schools/mine` puis `GET /classrooms/{schoolId}/list` et chargement élèves `GET /classrooms/{classId}/list/pupils` (accordéons).
- **Dashboard Prof** → mêmes appels, mais restreints aux classes dont il est maître.
- **Fiche élève (onglets)**
  - Notes : `GET /pupils/{id}/grades`, ajout `POST /pupils/{id}/grades/add`
  - Absences : `GET /pupils/{id}/absences`, ajout `POST /pupils/{id}/absences/add`
  - Remarques : `GET /pupils/{id}/remarks`, ajout `POST /pupils/{id}/remarks/add`

## 2) Données utilisateur gérées

- **Login/Register** : champs requis, email valide, **password = confirmPassword**.
- **Dialogs d’ajout** :
  - Note : `matiere` non vide, `note` ∈ `[0,20]`, `dateNote` valide.
  - Absence : `date` requise, `halfDay` ∈ `{AM,PM}`, `justifie` booléen.
  - Remarque : `type` ∈ `{INFO,WARNING,SEVERE}`, `contenu` non vide.

## 3) Bugs corrigés / améliorations

- **JWT non injecté** → Interceptor Axios commun + suppression auto du header pour `/auth/*`.
- **404 proxy** → `VITE_API_URL=http://localhost:4700` + proxy Vite pour `/auth`, `/schools`, `/classrooms`, `/pupils`.
- **DTO reçus `null`** → forcer `Content-Type: application/json` + noms de champs alignés (records Java).
- **Accordéons** → état `expanded` par `classId` (toggle fiable).
- **Cache `whoami`** → réponse `Cache-Control: no-store`.
- **Types SQL** → notes en `DECIMAL(5,2)` + `BigDecimal`.

## 4) Sécurité renforcée (résumé)

- **JWT stateless** (Spring Security + filtre `JwtAuthFilter`).
- **RBAC** :
  - Prof : accès uniquement aux classes où il est maître (inscriptions **actives**).
  - Principal : uniquement ses écoles.
  - Vérifications côté **services** via `InscriptionRepository`.
- **Erreurs claires** : 401 → redirection `/login?msg=expired`; 403 → message d’accès refusé.
