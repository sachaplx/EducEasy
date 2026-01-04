# Educ’Easy — Rapport de tests (synthèse)

## 1) Liste des tests effectués (format simple)

- **Login avec username** → OK (identifier=username + password).
- **Login avec email** → OK (identifier=email + password).
- **Token expiré** → OK (401 → redirection /login?msg=expired).
- **Register (PRINCIPAL)** → OK (compte créé, rôle correct).
- **Register (TEACHER)** → OK (compte + professor lié).
- **Principal : /schools/mine** → OK (retourne uniquement ses écoles).
- **Classes par école** → OK (/classrooms/{schoolId}/list).
- **Élèves par classe (accordéon)** → OK (/classrooms/{classId}/list/pupils).
- **Fiche élève — notes** (GET) → OK ; **ajout** (POST /grades/add) → OK (0..20).
- **Fiche élève — absences** (GET) → OK ; **ajout** (POST /absences/add) → OK.
- **Fiche élève — remarques** (GET) → OK ; **ajout** (POST /remarks/add) → OK.
- **RBAC prof hors périmètre** → OK (403).
- **RBAC principal hors périmètre** → OK (403).
- **/auth/whoami** sans 304 → OK (no-store).
- **Proxy Vite** (auth/schools/classrooms/pupils) → OK.

## 2) Résultats (résumé)

- Auth et propagation JWT stables (interceptor Axios).
- Dashboards : chargements cohérents (écoles → classes → élèves).
- Fiche élève : lecture/ajout fonctionnels sur les 3 onglets.
- RBAC validé côté services (périmètres prof/principal).

## 3) Captures (optionnel)

`captures/01_login.png`, `02_dashboard_principal.png`, `03_classes.png`, `04_pupil_profile.png`, `05_add_note.png`.

## 4) Cas non testés (et pourquoi)

- Export CSV (notes/absences) — non implémenté.
- Pagination/filtres avancés élèves — manque de temps.
- Tests automatisés (JUnit/Integration) — à planifier.
- SSO/Multi-annuaires — hors périmètre MVP.

## 5) Bugs/anomalies connus

- Alignement UI mineur en responsive serré.
- Messages d’erreur back parfois génériques.
- État d’accordéon après hard refresh : minime.

## 6) Corrections apportées

- Injection JWT uniforme (interceptor) + suppression sur `/auth/*`.
- Proxy/API URL corrigés (VITE_API_URL + proxy Vite).
- DTO `null` corrigés (Content-Type JSON + champs alignés).
- Types SQL des notes → `DECIMAL(5,2)` + `BigDecimal`.
- `whoami` en `Cache-Control: no-store`.
- Gestion fiable du toggle accordéon (par `classId`).

## 7) Sécurité (preuves concrètes)

- **JWT stateless** (filtre `JwtAuthFilter`, sessions désactivées).
- **RBAC** : `@PreAuthorize` + vérifs métier (`existsActiveForTeacher/Principal`).
- **Validation serveur** : notes bornées (0..20), enums stricts (HalfDay/RemarkLevel), dates valides.
- **Gestion 401** : redirection contrôlée vers Login (pas d’état zombie).

## 8) Conclusion

MVP fonctionnel et stable : auth, dashboards, fiche élève (lecture + création).  
RBAC et validations OK ; bugs bloquants corrigés.  
Reste à faire : exports, pagination/filtres, tests auto.
