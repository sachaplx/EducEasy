# Vue d’ensemble (3 couches)

- Présentation (Front) : Vue3 + Vuetify, Router + Pinia.
- Métier (Back Services) : AuthService, PupilService, ClassroomService, NoteService, AbsenceService, RemarqueService…
- Données (JPA/Repositories) : UserRepository, PupilRepository, InscriptionRepository, ClassroomRepository, SchoolRepository, NoteRepository, AbsenceRepository, RemarqueRepository.

Flux : UI → REST → Services (validations/règles & contrôle d’accès) → Repos (JPA) → DB MySQL.

# Sécurité

- JWT + JwtAuthFilter, SecurityFilterChain stateless.
- Hashage mots de passe vie BCrypt
- UserDetailsService support username ou email.
- Guards front (401 → /login), ajout auto du token Axios.

# Patterns

- DTO Info/Request pour I/O (NoteInfo, AbsenceInfo, RemarqueInfo, PupilInfo, ClassroomInfo…).
- Services centralisent la logique (droits + validations) et appellent les Repos.

## Choix des outils

- **Front** : Vue 3 + Vite + Vuetify, Pinia (store), Axios (interceptor JWT).
- **Back** : Spring Boot 3, Spring Security, Spring Data JPA + Hibernate.
- **DB** : MySQL 8 (HikariCP).
- **ORM** : Hibernate (DTO dédiés côté API).

## Bonnes pratiques — Sécurité

- **Auth** : JWT stateless (Bearer), mots de passe **BCrypt**.
- **RBAC** : rôles `PRINCIPAL` / `TEACHER` + `@PreAuthorize`.
- **Contrôles métier** : vérifs de périmètre en services (élève ∈ classe/école).
- **Validation** : front (Vuetify rules) + back (bornes, enums, dates).
- **Headers** : `whoami` en **no-store** ; proxy Vite en dev ; erreurs sobres.
- **Données** : types précis (`DECIMAL(5,2)`), FKs, transactions.

## Bonnes pratiques — Éco-conception

- **Réseau** : chargement à la demande (élèves par classe), filtres côté serveur.
- **Payload** : DTO ciblés, pas d’images lourdes, cache lecture quand possible.
- **Build** : Vite (minify/treeshake), code-splitting si nécessaire.
- **DB** : requêtes sélectives, index utiles, éviter N+1.
- **Infra** : stateless → scale simple ; logs au niveau utile.
