CREATE TABLE password_reset_token (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  token VARCHAR(255) NOT NULL,
  user_id BIGINT UNSIGNED NOT NULL,
  expiry_date DATETIME(6) NOT NULL,
  used TINYINT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  CONSTRAINT uk_password_reset_token_token UNIQUE (token),
  CONSTRAINT fk_password_reset_token_user
    FOREIGN KEY (user_id) REFERENCES educeasy_user (id)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;
