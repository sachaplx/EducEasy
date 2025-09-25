# Entités principales

- User(id, username, email, password, role{PRINCIPAL,TEACHER}, actif, createdAt)
- Principal(id, user_id, firstName, lastName)
- Professor(id, user_id, nom, prenom)
- School(id, nom, ville, codePostal, adresse, principal_id)
- Classroom(id, level, school_id, professor_id)
- Pupil(id, nom, prenom, gender{BOY,GIRL})
- Inscription(id, pupil_id, classroom_id, dateEntree, dateSortie nullable)
- Note(id, pupil_id, matiere, note DECIMAL(5,2), dateNote, commentaire)
- Absence(id, pupil_id, date, halfDay TINYINT via converter, justifie, motif)
- Remarque(id, pupil_id, auteur_user_id (FK User), type{INFO,WARNING}, contenu, createdAt)

# Règles

- Élève “actif” dans une classe si dateEntree <= CURDATE() et (dateSortie IS NULL ou > CURDATE()).
- Visibilité Instituteur: via Inscription sur ses Classroom.
- Visibilité Directeur: via School.principal_id.
