CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255),
    auth_provider VARCHAR(20) NOT NULL DEFAULT 'LOCAL',
    google_id VARCHAR(255),

    PRIMARY KEY (id),

    UNIQUE KEY uk_users_email (email),
    UNIQUE KEY uk_users_phone_number (phone_number),
    UNIQUE KEY uk_users_username (username),
    UNIQUE KEY uk_users_google_id (google_id)
);