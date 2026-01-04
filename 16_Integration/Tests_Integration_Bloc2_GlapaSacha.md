# 1) Structure du projet

## Technos clefs

- Backend : Java 17, Spring Boot (Web, Security), Spring Data JPA (Hibernate), Bean Validation.
- Front : Vite + Vue 3 + Vue Router, fetch/axios.
- Base de données : MySQL/MariaDB (ou H2 tests).
- AuthN/AuthZ : Spring Security.

# 2) Composants et connexions

## Backend (REST)

- Controllers

  - GET /pupils/{pupilId}/grades — liste des notes (filtres: matière, période).

  - POST /pupils/{pupilId}/grades/add — ajout d’une note (rôles: TEACHER, PRINCIPAL).

  - GET /pupils/{pupilId}/absences — liste des absences (filtres: dates, AM/PM, justifiée).

  - POST /pupils/{pupilId}/absences/add — ajout d’absence (TEACHER, PRINCIPAL).

  - GET /pupils/{pupilId}/remarks — liste des remarques (filtre: niveau).

  - POST /pupils/{pupilId}/remarks/add — ajout remarque (TEACHER, PRINCIPAL).

- Services

  - NoteService.addNote(pupilId, req, auth)

  1. Récupère l’utilisateur courant via UserRepository.findByUsername(auth.getName()).

  2. Vérifie l’appartenance élève ↔ enseignant/principal via InscriptionRepository.existsActiveForTeacher/Principal(...).

  3. Valide la note (0..20), dateNote, etc.

  4. Persiste puis retourne un NoteInfo (DTO).

  - Services Absence/Remarque sur le même modèle.

- Repositories

  - NoteRepository : requêtes paramétrées (Spring Data), méthodes filtrées par pupilId, matiere, période.

  - InscriptionRepository : garde-fous d’accès (enseignant/principal autorisés).

  - UserRepository, PupilRepository, etc.

- Sécurité

  - Spring Security :

    - Authentification (mock en tests, réelle en prod).

    - Autorisations par annotations @PreAuthorize("hasAnyRole('TEACHER','PRINCIPAL')").

    - CSRF activé sur POST (tests utilisent .with(csrf())).

### Front (Vue)

- Pages

  - PupilGrades.vue : affiche notes + formulaire d’ajout → POST /grades/add, reload via reloadNotes().

  - Absences.vue : idem, POST /absences/add, reloadAbsences().

  - Remarks.vue : idem, POST /remarks/add, reloadRemarks().

- Services API

  - api/notes.ts : list(pupilId, filters), add(pupilId, payload).

  - Idem pour absences & remarques.

- Router & Guards

  - Routes /pupils/:id/grades|absences|remarks.

  - Garde simple (ex: vérifie session/jwt si présent).

- Validation front

  - Contrôles de base (champs requis, bornes 0..20 pour note).

  - Retourne les erreurs serveur (HTTP 400/403) sous forme de toast/alerte.

# 3) Flux d’échange (simplifié)

1. UI (formulaire “Ajouter une note”) → Front Service (POST /pupils/{id}/grades/add JSON).

2. Controller mappe @RequestBody CreateNoteRequest → Service.

3. Service:

   - resolve user courant → vérif accès via InscriptionRepository;

   - validation métier → persistance via NoteRepository;

   - retour DTO NoteInfo.

4. Controller → HTTP 200 + JSON → Front.

5. Front: notifie succès + reloadNotes().

# 4) Capture / extrait de navigation & retours (placer vos preuves)

- Navigation :

  - Tableau des élèves → Détail Élève → onglets : Notes / Absences / Remarques.

  - Bouton “Ajouter” ouvre un mini-form (matière, note, date, commentaire).

- Retour utilisateur (extraits à compléter) :

  - “Ajout de note rapide, les champs sont clairs.”

  - “Message d’erreur explicite quand je dépasse 20/20.”

  - “403 quand je change l’URL d’un élève hors de ma classe (OK sécurité).”

# 5) Points de sécurité appliqués

- Autorisation par rôle sur les endpoints d’ajout (@PreAuthorize).

- Contrôle d’appartenance (enseignant/principal ↔ élève) en service via InscriptionRepository.

- Validation stricte : bornes note ∈ [0;20], dates ISO, champs requis.

- CSRF actif (front envoie le token ; en tests : .with(csrf())).

- Paramétrage ORM (Spring Data / Hibernate) → pas de concat SQL.

- Erreurs propres : 400 validation, 403 accès, 401 si non authentifié.

# 6) Points d’attention / dettes techniques

- Tests :

  - WebMvc tests ok (200 add, 401/403 non-auth/forbidden).

  - Unitaires service : couvrir cas bornes (−1, 0, 20, 20.1), dates futures si interdites.

- Front :

  - Harmoniser toasts/surfaces d’erreur.

  - Loader pendant reload\*().

- Perf :

  - Indices DB envisagés sur (pupil_id, date) et (pupil_id, matiere).

- Sécurité :

  - À faire si JWT : expiration/refresh, revocation list.
