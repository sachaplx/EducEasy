-- V0.0.0.0 - EDCS init schema (structure only)
-- Generated from existing MySQL schema export; no data, no flyway_schema_history, no verification_token tables.

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `educeasy_absence`;
DROP TABLE IF EXISTS `educeasy_note`;
DROP TABLE IF EXISTS `educeasy_inscription`;
DROP TABLE IF EXISTS `educeasy_remarque`;
DROP TABLE IF EXISTS `educeasy_classe`;
DROP TABLE IF EXISTS `educeasy_ecole`;
DROP TABLE IF EXISTS `educeasy_directeur`;
DROP TABLE IF EXISTS `educeasy_prof`;
DROP TABLE IF EXISTS `educeasy_eleve`;
DROP TABLE IF EXISTS `educeasy_user`;

CREATE TABLE IF NOT EXISTS `educeasy_absence` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `eleve_id` bigint UNSIGNED NOT NULL,
  `date` date NOT NULL,
  `demi_journee` tinyint NOT NULL,
  `motif` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `justifie` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_absence_eleve_date` (`eleve_id`,`date`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `educeasy_classe` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `ecole_id` bigint UNSIGNED NOT NULL,
  `nom` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `niveau` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `annee_scolaire` varchar(9) COLLATE utf8mb4_unicode_ci NOT NULL,
  `maitre_id` bigint UNSIGNED DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_classe_unique` (`ecole_id`,`nom`,`annee_scolaire`),
  KEY `fk_classe_maitre` (`maitre_id`),
  KEY `idx_classe_ecole` (`ecole_id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `educeasy_directeur` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` bigint UNSIGNED NOT NULL,
  `prenom` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nom` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`),
  KEY `idx_directeur_user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `educeasy_ecole` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `nom` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `adresse` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `code_postal` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ville` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `directeur_id` bigint UNSIGNED DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ecole_nom_ville` (`nom`,`ville`),
  KEY `idx_ecole_directeur` (`directeur_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `educeasy_eleve` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `nom` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `prenom` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `date_naissance` date DEFAULT NULL,
  `adresse` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `contact_parent` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `genre` enum('BOY','GIRL') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'BOY',
  PRIMARY KEY (`id`),
  KEY `idx_eleve_nom_prenom` (`nom`,`prenom`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `educeasy_inscription` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `eleve_id` bigint UNSIGNED NOT NULL,
  `classe_id` bigint UNSIGNED NOT NULL,
  `date_entree` date NOT NULL,
  `date_sortie` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_inscription_eleve` (`eleve_id`,`date_sortie`),
  KEY `idx_inscription_classe` (`classe_id`,`date_sortie`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `educeasy_note` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `eleve_id` bigint UNSIGNED NOT NULL,
  `matiere` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `note` decimal(5,2) NOT NULL,
  `commentaire` text COLLATE utf8mb4_unicode_ci,
  `date_saisie` date NOT NULL DEFAULT (curdate()),
  PRIMARY KEY (`id`),
  KEY `idx_note_eleve_tri` (`eleve_id`)
) ENGINE=InnoDB AUTO_INCREMENT=132 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `educeasy_prof` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` bigint UNSIGNED NOT NULL,
  `nom` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `prenom` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `telephone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_prof_user` (`user_id`),
  KEY `idx_prof_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `educeasy_remarque` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `eleve_id` bigint UNSIGNED NOT NULL,
  `type` enum('INFO','ALERTE','DISCIPLINE') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'INFO',
  `contenu` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `date_saisie` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `auteur_user_id` bigint UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_remarque_eleve` (`eleve_id`),
  KEY `idx_remarque_auteur_user` (`auteur_user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=65 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `educeasy_user` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `email` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `username` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password_hash` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `role` enum('PRINCIPAL','TEACHER') COLLATE utf8mb4_unicode_ci NOT NULL,
  `actif` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_email` (`email`),
  UNIQUE KEY `uk_user_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

ALTER TABLE `educeasy_absence`
  ADD CONSTRAINT `fk_absence_eleve` FOREIGN KEY (`eleve_id`) REFERENCES `educeasy_eleve` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT;

ALTER TABLE `educeasy_classe`
  ADD CONSTRAINT `fk_classe_ecole` FOREIGN KEY (`ecole_id`) REFERENCES `educeasy_ecole` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  ADD CONSTRAINT `fk_classe_maitre` FOREIGN KEY (`maitre_id`) REFERENCES `educeasy_prof` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT;

ALTER TABLE `educeasy_directeur`
  ADD CONSTRAINT `fk_principal_user` FOREIGN KEY (`user_id`) REFERENCES `educeasy_user` (`id`);

ALTER TABLE `educeasy_ecole`
  ADD CONSTRAINT `fk_school_directeur` FOREIGN KEY (`directeur_id`) REFERENCES `educeasy_directeur` (`id`);

ALTER TABLE `educeasy_inscription`
  ADD CONSTRAINT `fk_inscription_classe` FOREIGN KEY (`classe_id`) REFERENCES `educeasy_classe` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  ADD CONSTRAINT `fk_inscription_eleve` FOREIGN KEY (`eleve_id`) REFERENCES `educeasy_eleve` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE `educeasy_note`
  ADD CONSTRAINT `fk_note_eleve` FOREIGN KEY (`eleve_id`) REFERENCES `educeasy_eleve` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT;

ALTER TABLE `educeasy_prof`
  ADD CONSTRAINT `fk_prof_user` FOREIGN KEY (`user_id`) REFERENCES `educeasy_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT;

ALTER TABLE `educeasy_remarque`
  ADD CONSTRAINT `fk_remarque_auteur_user` FOREIGN KEY (`auteur_user_id`) REFERENCES `educeasy_user` (`id`),
  ADD CONSTRAINT `fk_remarque_eleve` FOREIGN KEY (`eleve_id`) REFERENCES `educeasy_eleve` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT;

SET FOREIGN_KEY_CHECKS = 1;
