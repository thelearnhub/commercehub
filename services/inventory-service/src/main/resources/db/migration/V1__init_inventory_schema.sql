CREATE TABLE inventory (
    id                 BINARY(16)   NOT NULL PRIMARY KEY,
    sku                VARCHAR(100) NOT NULL UNIQUE,
    available_quantity INT          NOT NULL DEFAULT 0,
    reserved_quantity  INT          NOT NULL DEFAULT 0,
    allocated_quantity INT          NOT NULL DEFAULT 0,
    created_at         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_inventory_sku (sku)
);

CREATE TABLE inventory_audit_logs (
    id             BINARY(16)   NOT NULL PRIMARY KEY,
    sku            VARCHAR(100) NOT NULL,
    operation_type VARCHAR(50)  NOT NULL,
    quantity       INT          NOT NULL,
    reference_id   VARCHAR(100) NULL,
    reason         VARCHAR(255) NULL,
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_audit_sku (sku),
    INDEX idx_audit_reference (reference_id)
);
