# Documentation métier — Educ’Easy

## Services

### AuthService

- login(identifier, password) → JWT + info user.
- register(req) → création user + entité jointe (Principal ou Professor).

### ClassroomService

- listBySchool(schoolId) → ClassroomInfo (teacherFirstName/LastName).
- activePupilsOfClassroom(classroomId) → PupilInfo.

### PupilService

- Projection Pupil → PupilInfo (classe & école via Inscription active).

### NoteService / AbsenceService / RemarqueService

- Ajout soumis au contrôle d’accès :
  - TEACHER : élève appartenant à sa classe active.
  - PRINCIPAL : élève rattaché à l’une de ses écoles actives.
