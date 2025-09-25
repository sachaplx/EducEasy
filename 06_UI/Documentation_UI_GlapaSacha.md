# Documentation UI — Educ’Easy

## Pages

- Login.vue / Register.vue
- Home.vue (vue selon rôle)
- PrincipalDashboard.vue (sélection école → accordéon classes → élèves)
- PupilProfile.vue (onglets Notes/Absences/Remarques + dialogs d’ajout)

## Composants

- AppNavbar.vue (brand centré, liens à gauche, badge rôle à droite, SearchBar si auth)
- Dialogs: AddNoteDialog, AddAbsenceDialog, AddRemarkDialog

## Règles UX

- Desktop-first responsive, thèmes Vuetify (barre bleue, fond clair).
- Avatars élèves colorés selon genre.
