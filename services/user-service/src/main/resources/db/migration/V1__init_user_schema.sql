CREATE TABLE user_profiles (
    id          BINARY(16)   NOT NULL PRIMARY KEY,
    email       VARCHAR(255) NOT NULL,
    first_name  VARCHAR(100) NOT NULL,
    last_name   VARCHAR(100) NOT NULL,
    phone       VARCHAR(20)  NULL,
    avatar_url  VARCHAR(512) NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uq_user_profiles_email UNIQUE (email)
);

CREATE INDEX idx_user_profiles_email ON user_profiles (email);
