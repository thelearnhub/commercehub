CREATE TABLE addresses (
    id          BINARY(16)   NOT NULL PRIMARY KEY,
    user_id     BINARY(16)   NOT NULL,
    label       VARCHAR(50)  NOT NULL,
    street      VARCHAR(255) NOT NULL,
    city        VARCHAR(100) NOT NULL,
    state       VARCHAR(100) NOT NULL,
    zip_code    VARCHAR(20)  NOT NULL,
    country     VARCHAR(100) NOT NULL,
    is_default  BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_addresses_user FOREIGN KEY (user_id) REFERENCES user_profiles (id),
    INDEX idx_addresses_user_id (user_id)
);
