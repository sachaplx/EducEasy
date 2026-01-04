# Objectifs

- Plateforme web de gestion scolaire sécurisée (multi-rôles).
- Directeur : voir ses écoles, leurs classes, les élèves, et consulter/saisir.
- Instituteur : voir ses classes, leurs élèves, saisir notes/absences/remarques.

# Acteurs & droits

- Directeur : accès aux données des écoles dont il est responsable (filtrage par Inscription/School).
- Instituteur : accès uniquement aux classes où il enseigne.
- Non authentifié : redirection Login.

# Périmètre fonctionnel

- Auth: /auth/login, /auth/register.
- Écoles (mine), classes par école, élèves par classe.
- Fiche élève : listes + création Notes, Absences, Remarques via dialogs.
- Recherche globale d’élève (autosuggest).

# Exclusions

Parents/élèves externes, bulletins PDF, notifications e-mail, import CSV massif.

# Contraintes techniques

- RBAC back & front, stateless JWT, CORS, proxy dev.
- Validations DTO (dates, ranges notes 0..20, enums).
- UI responsive desktop-first + Vuetify.

# Risques

- Propagation de droits (vérifications côté service).

- Cohérence référentielle (FK & cascade).

- Performance requêtes lazy → fetch ciblés.
