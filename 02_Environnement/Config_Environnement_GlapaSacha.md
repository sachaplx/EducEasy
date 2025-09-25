# IDE & Outils

- Backend: IntelliJ / Eclipse, JDK 17, Maven.
- Frontend: VS Code, Node LTS, Vite.
- DB: MySQL 8, client (TablePlus/DBeaver).
- Test API: Insomnia.

# Dépendances clés (Back)

Spring Boot 3.5.x, spring-boot-starter-web, spring-boot-starter-security, spring-boot-starter-data-jpa, jjwt (ou io.jsonwebtoken), MySQL driver.

# Dépendances clés (Front)

Vue 3, Vue Router, Pinia, Vuetify 3, Axios.

## Structure (Back)

```
src/main/java/com/educeasy/core
  ├─ controller (AuthController, PupilController, ClassroomController, SchoolController…)
  ├─ service (AuthService, PupilService, NoteService, AbsenceService, RemarqueService, ClassroomService…)
  ├─ repository (UserRepository, PupilRepository, InscriptionRepository, ClassroomRepository, SchoolRepository…)
  ├─ security (SecurityConfig, JwtAuthFilter, JwtService, AuthBeans)
  └─ entity (User, Principal, Professor, Pupil, School, Classroom, Inscription, Note, Absence, Remarque, enums…)
```

## Structure (Front)

```
src/
  ├─ components/ (AppNavbar.vue, SearchBar.vue, dialogs/*)
  ├─ pages/ (Login.vue, Register.vue, Home.vue, PrincipalDashboard.vue, PupilProfile.vue)
  ├─ stores/ (auth.js)
  ├─ services/ (api.js)
  └─ router/ (index.js + guards)

```
