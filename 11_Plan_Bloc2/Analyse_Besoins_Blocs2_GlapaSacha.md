# Educ’Easy — Rappel & Cadrage Bloc 2

## 1) Rappel du contexte projet

**Objectifs.** Simplifier la gestion scolaire (écoles, classes, élèves) et le suivi pédagogique (notes, absences, remarques) au quotidien.  
**Type d’application.** Web app **SPA** (Vue 3 + Vuetify) avec **API REST** (Spring Boot), base **MySQL**, auth **JWT** stateless, **RBAC** (PRINCIPAL / TEACHER).

---

## 2) Résumé Bloc 1 — Fonctionnalités déjà développées

- **Auth**
  - Login par _identifier_ (email **ou** username) + mot de passe (BCrypt).
  - Register (rôles `PRINCIPAL`/`TEACHER`) avec création des entités liées.
  - Stockage/propagation **JWT**, redirection auto si 401 (token expiré).
- **Dashboards**
  - **Principal** : lister _ses_ écoles, voir les classes par école.
  - **Teacher** : lister _ses_ classes (maîtrise de classe).
- **Fiche élève**
  - Consultations : **notes**, **absences**, **remarques** (onglets).
  - Créations via **dialogs** (validation front) reliés aux endpoints POST.
- **RBAC & périmètre**
  - Prof : actions limitées aux classes où il est maître (inscription active).
  - Principal : actions limitées aux écoles qu’il dirige.
- **Tech**
  - Alias `@` (Vite), proxy dev → API, fond global, SearchBar conditionnelle.
  - DTOs : `PupilInfo`, `NoteInfo`, `AbsenceInfo`, `RemarqueInfo`, `ClassroomInfo`, `SchoolInfo`.

---

## 3) Liste des acteurs — Rôles, droits, interactions

- **PRINCIPAL (Directeur)**
  - Droits : gérer ses **écoles**, **classes**, consulter/ajouter éléments élèves (dans son périmètre).
  - Interactions : vue des écoles → classes → élèves ; ajout notes/absences/remarques.
- **TEACHER (Instituteur)**
  - Droits : gérer uniquement **ses classes** et **leurs élèves** (inscriptions actives).
  - Interactions : vue classes → élèves ; ajout notes/absences/remarques.
- **Système**
  - Vérifie JWT, rôle, et **périmètre** via services (requêtes d’existence dans les repositories).

---

## 4) Fonctionnalités Bloc 2 — Ciblées pour cette phase

- **Fiche élève (consolidation)**
  - Filtres simples : dates/matière (notes), dates/justifié/demi-journée (absences), type (remarques).
  - UX : dialogues intégrés par onglet (cohérence + retour visuel).
- **Dashboard Principal (UX)**
  - Chargement auto des écoles à l’arrivée ; accordéons classes stables (toggle).
- **RBAC renforcé**
  - Double-check métier systématique (teacher vs principal) avant toute création/lecture sensible.
- **Stabilité & erreurs**
  - Messages front lisibles (erreurs 4xx), états `loading/disabled`, no-store sur `whoami`.

---

## 5) Traitements associés — Enregistrements, filtres, sécurité

- **Enregistrements**
  - `POST /pupils/{id}/grades/add` → validation `0..20` (BigDecimal), date, matière non vide.
  - `POST /pupils/{id}/absences/add` → date requise, `halfDay` (AM/PM), `justifie` booléen.
  - `POST /pupils/{id}/remarks/add` → `type` (INFO/WARNING/SEVERE), contenu non vide.
- **Filtres (GET)**
  - Notes : `matiere`, `from`, `to`.
  - Absences : `from`, `to`, `halfDay`, `justifie`.
  - Remarques : `type`.
- **Sécurité**
  - **JWT** en en-tête `Authorization`. Sessions **STATELESS**.
  - **@PreAuthorize** sur POST ; vérif de périmètre en **services** via `InscriptionRepository`:
    - `existsActiveForTeacher(pupilId, teacherUserId)`
    - `existsActiveForPrincipal(pupilId, principalUserId)`
  - **Whoami no-store** (anti-cache).
  - Mots de passe **BCrypt** (PasswordEncoder Spring).

---

## 6) Données manipulées — Types, sources, visibilité

- **Élève (Pupil)** : `nom`, `prenom`, `genre` (enum `BOY`/`GIRL`), id.
- **Inscription** : lien élève ↔ classe (dates entrée/sortie pour “actif”).
- **Classe (Classroom)** : `level`, maître (Professor), id, école.
- **École (School)** : `nom`, `ville`, principal (Principal).
- **Note** : `matiere` (string), `note` (DECIMAL/BigDecimal), `dateNote`, `commentaire`.
- **Absence** : `date`, `halfDay` (TINYINT ↔ enum), `justifie`, `motif`.
- **Remarque** : `type` (enum), `contenu`, `createdAt`, auteur (Teacher **ou** Principal).
- **Visibilité** : filtrée par **RBAC** + **périmètre** (inscriptions actives / écoles du principal).

---

## 7) Contraintes techniques — Architecture multicouche

- **Back**
  - Spring Boot 3, Java 17, Hibernate 6, **MySQL**.
  - Entités ↔ DTO (mappers dans services), **services** = logique métier + RBAC, **repositories** = accès données.
  - Types précis (ex. `DECIMAL(5,2)` + `BigDecimal`) ; enums mappées (HalfDay TINYINT, RemarkLevel STRING).
  - **Stateless** + filtre JWT ; no-cache sur endpoints sensibles.
- **Front**
  - Vue 3 (Composition API), Vuetify, **Pinia** (auth), Axios (instance + interceptors).
  - Alias Vite `@` → `src`, proxy dev vers `:4700`.
  - Composants : Navbar, Dashboards, PupilProfile (onglets + dialogs).

---

## 8) Périmètre de travail — Bloc 2 (inclus / exclu)

**Inclus**

- Finalisation Fiche Élève (filtres + dialogs par onglet).
- Renfort RBAC (vérifs métier systématiques) et UX d’erreurs.
- Amélioration dashboard Principal (auto-load écoles, accordéons classes stables).
- Documentation courte (README “Installation rapide”, tests synthèse).

**Exclus (reportés)**

- Exports CSV (notes/absences/remarques).
- Pagination/filtres avancés sur listes élèves.
- Tests automatisés (JUnit/Integration), CI/CD.
- SSO / annuaires externes, rôles supplémentaires.

---
