CREATE TABLE educeasy_invitation (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(150) NOT NULL,
  token VARCHAR(255) NOT NULL,
  classroom_id BIGINT UNSIGNED NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  expires_at TIMESTAMP NULL,
  used_at TIMESTAMP NULL,

  CONSTRAINT fk_invite_classroom FOREIGN KEY (classroom_id) REFERENCES educeasy_classe(id),
  UNIQUE KEY uk_invite_token (token),
  KEY idx_invite_email (email),
  KEY idx_invite_classroom (classroom_id),
  KEY idx_invite_active (email, classroom_id, used_at)
);
