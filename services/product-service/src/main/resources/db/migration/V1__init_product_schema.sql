CREATE TABLE categories (
    id          BINARY(16)   NOT NULL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255) NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE products (
    id             BINARY(16)     NOT NULL PRIMARY KEY,
    name           VARCHAR(255)   NOT NULL,
    sku            VARCHAR(100)   NOT NULL UNIQUE,
    description    TEXT           NULL,
    base_price     DECIMAL(10, 2) NOT NULL,
    stock_quantity INT            NOT NULL DEFAULT 0,
    category_id    BINARY(16)     NOT NULL,
    created_at     TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories (id),
    INDEX idx_products_sku (sku),
    INDEX idx_products_category (category_id)
);
