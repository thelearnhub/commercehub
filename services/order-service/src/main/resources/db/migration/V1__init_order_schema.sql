CREATE TABLE orders (
    id               BINARY(16)     NOT NULL PRIMARY KEY,
    user_email       VARCHAR(255)   NOT NULL,
    status           VARCHAR(50)    NOT NULL,
    total_amount     DECIMAL(10, 2) NOT NULL,
    shipping_address VARCHAR(512)   NOT NULL,
    payment_method   VARCHAR(50)    NOT NULL,
    created_at       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_orders_user_email (user_email),
    INDEX idx_orders_status (status)
);

CREATE TABLE order_items (
    id           BINARY(16)     NOT NULL PRIMARY KEY,
    order_id     BINARY(16)     NOT NULL,
    sku          VARCHAR(100)   NOT NULL,
    product_name VARCHAR(255)   NOT NULL,
    unit_price   DECIMAL(10, 2) NOT NULL,
    quantity     INT            NOT NULL,
    total_price  DECIMAL(10, 2) NOT NULL,
    created_at   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE,
    INDEX idx_order_items_order_id (order_id),
    INDEX idx_order_items_sku (sku)
);
