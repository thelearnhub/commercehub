-- Seed Dummy Users for Auth Service
-- Password for all accounts: Password123!
-- BCrypt Hash: $2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xD0m1bC.iW/A5oee

INSERT INTO users (id, email, password_hash, role, auth_provider, enabled, created_at)
VALUES 
    -- Admin User
    (UNHEX('11111111111111111111111111111111'), 'admin@commercehub.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xD0m1bC.iW/A5oee', 'ADMIN', 'LOCAL', TRUE, CURRENT_TIMESTAMP),
    
    -- Seller User
    (UNHEX('22222222222222222222222222222222'), 'seller@commercehub.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xD0m1bC.iW/A5oee', 'SELLER', 'LOCAL', TRUE, CURRENT_TIMESTAMP),
    
    -- Customer User 1
    (UNHEX('33333333333333333333333333333333'), 'customer@commercehub.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xD0m1bC.iW/A5oee', 'CUSTOMER', 'LOCAL', TRUE, CURRENT_TIMESTAMP),
    
    -- Customer User 2
    (UNHEX('44444444444444444444444444444444'), 'john.doe@example.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xD0m1bC.iW/A5oee', 'CUSTOMER', 'LOCAL', TRUE, CURRENT_TIMESTAMP),
    
    -- Customer User 3
    (UNHEX('55555555555555555555555555555555'), 'jane.smith@example.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xD0m1bC.iW/A5oee', 'CUSTOMER', 'LOCAL', TRUE, CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE email = VALUES(email);
