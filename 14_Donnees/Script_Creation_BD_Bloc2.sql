-- Script de création MySQL — Educ’Easy (extrait minimal)

CREATE TABLE educeasy_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(100) UNIQUE NOT NULL,
  email VARCHAR(255) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  role ENUM('PRINCIPAL','TEACHER') NOT NULL,
  actif BOOLEAN NOT NULL DEFAULT TRUE,
  created_at DATETIME NOT NULL
);

CREATE TABLE educeasy_principal (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL UNIQUE,
  first_name VARCHAR(100) NOT NULL,
  last_name  VARCHAR(100) NOT NULL,
  CONSTRAINT fk_principal_user FOREIGN KEY (user_id) REFERENCES educeasy_user(id)
);

CREATE TABLE educeasy_professor (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL UNIQUE,
  nom VARCHAR(100) NOT NULL,
  prenom VARCHAR(100) NOT NULL,
  CONSTRAINT fk_prof_user FOREIGN KEY (user_id) REFERENCES educeasy_user(id)
);

CREATE TABLE educeasy_school (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  nom VARCHAR(255) NOT NULL,
  ville VARCHAR(255),
  code_postal VARCHAR(20),
  adresse VARCHAR(255),
  principal_id BIGINT NOT NULL,
  CONSTRAINT fk_school_principal FOREIGN KEY (principal_id) REFERENCES educeasy_principal(id)
);

CREATE TABLE educeasy_classroom (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  level VARCHAR(50) NOT NULL,
  school_id BIGINT NOT NULL,
  professor_id BIGINT NOT NULL,
  CONSTRAINT fk_classroom_school FOREIGN KEY (school_id) REFERENCES educeasy_school(id),
  CONSTRAINT fk_classroom_prof FOREIGN KEY (professor_id) REFERENCES educeasy_professor(id)
);

CREATE TABLE educeasy_pupil (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  nom VARCHAR(100) NOT NULL,
  prenom VARCHAR(100) NOT NULL,
  gender ENUM('BOY','GIRL') NOT NULL
);

CREATE TABLE educeasy_inscription (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  pupil_id BIGINT NOT NULL,
  classroom_id BIGINT NOT NULL,
  date_entree DATE NOT NULL,
  date_sortie DATE NULL,
  CONSTRAINT fk_insc_pupil FOREIGN KEY (pupil_id) REFERENCES educeasy_pupil(id),
  CONSTRAINT fk_insc_class FOREIGN KEY (classroom_id) REFERENCES educeasy_classroom(id)
);

CREATE TABLE educeasy_note (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  eleve_id BIGINT NOT NULL,
  matiere VARCHAR(100) NOT NULL,
  note DECIMAL(5,2) NOT NULL,
  date_saisie DATE NOT NULL,
  commentaire TEXT NULL,
  CONSTRAINT fk_note_pupil FOREIGN KEY (eleve_id) REFERENCES educeasy_pupil(id)
);

CREATE TABLE educeasy_absence (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  eleve_id BIGINT NOT NULL,
  date DATE NOT NULL,
  demi_journee TINYINT NOT NULL,
  justifie BOOLEAN NOT NULL DEFAULT FALSE,
  motif VARCHAR(255),
  CONSTRAINT fk_abs_pupil FOREIGN KEY (eleve_id) REFERENCES educeasy_pupil(id)
);

CREATE TABLE educeasy_remarque (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  eleve_id BIGINT NOT NULL,
  auteur_user_id BIGINT NOT NULL,
  type ENUM('INFO','WARNING') NOT NULL,
  contenu TEXT NOT NULL,
  date_saisie DATETIME NOT NULL,
  CONSTRAINT fk_rem_pupil FOREIGN KEY (eleve_id) REFERENCES educeasy_pupil(id),
  CONSTRAINT fk_rem_user FOREIGN KEY (auteur_user_id) REFERENCES educeasy_user(id)
);
