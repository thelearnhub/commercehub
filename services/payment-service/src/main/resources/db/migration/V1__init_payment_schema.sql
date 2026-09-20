CREATE TABLE payments (
    id              BINARY(16)     NOT NULL PRIMARY KEY,
    order_id        VARCHAR(100)   NOT NULL,
    user_id         VARCHAR(100)   NULL,
    amount          DECIMAL(12, 2) NOT NULL,
    currency        VARCHAR(10)    NOT NULL DEFAULT 'USD',
    provider        VARCHAR(50)    NOT NULL,
    payment_method  VARCHAR(50)    NOT NULL,
    status          VARCHAR(50)    NOT NULL,
    transaction_id  VARCHAR(255)   NULL,
    idempotency_key VARCHAR(255)   NULL,
    failure_reason  VARCHAR(512)   NULL,
    created_at      TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_payments_order_id (order_id),
    INDEX idx_payments_idempotency_key (idempotency_key)
);

CREATE TABLE payment_audit_logs (
    id         BINARY(16)     NOT NULL PRIMARY KEY,
    payment_id BINARY(16)     NOT NULL,
    action     VARCHAR(50)    NOT NULL,
    amount     DECIMAL(12, 2) NOT NULL,
    provider   VARCHAR(50)    NOT NULL,
    details    VARCHAR(512)   NULL,
    created_at TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_payment_audit_payment_id (payment_id)
);
