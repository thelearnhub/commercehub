-- Seed Dummy Orders & Order Items for Order Service

INSERT INTO orders (id, user_email, status, total_amount, shipping_address, payment_method, created_at)
VALUES
    -- Order 1: Delivered Order
    (UNHEX('e1111111111111111111111111111111'), 'customer@commercehub.com', 'DELIVERED', 2899.98, '742 Evergreen Terrace, Springfield, OR 97477, USA', 'CREDIT_CARD', CURRENT_TIMESTAMP),

    -- Order 2: Shipped Order
    (UNHEX('e2222222222222222222222222222222'), 'john.doe@example.com', 'SHIPPED', 193.00, '123 Main Street, Apt 4B, New York, NY 10001, USA', 'PAYPAL', CURRENT_TIMESTAMP),

    -- Order 3: Paid Order
    (UNHEX('e3333333333333333333333333333333'), 'jane.smith@example.com', 'PAID', 544.99, '456 Oak Avenue, Seattle, WA 98101, USA', 'CREDIT_CARD', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE status = VALUES(status);

INSERT INTO order_items (id, order_id, sku, product_name, unit_price, quantity, total_price, created_at)
VALUES
    -- Items for Order 1
    (UNHEX('f1111111111111111111111111111101'), UNHEX('e1111111111111111111111111111111'), 'LAP-MBP-16-M3', 'MacBook Pro 16" M3 Max', 2499.99, 1, 2499.99, CURRENT_TIMESTAMP),
    (UNHEX('f1111111111111111111111111111102'), UNHEX('e1111111111111111111111111111111'), 'AUD-SONY-XM5-BLK', 'Sony WH-1000XM5 Wireless Headphones', 399.99, 1, 399.99, CURRENT_TIMESTAMP),

    -- Items for Order 2
    (UNHEX('f2222222222222222222222222222201'), UNHEX('e2222222222222222222222222222222'), 'SHOE-NIKE-AF1-WHT-10', 'Nike Air Force 1 ''07 Sneaker', 115.00, 1, 115.00, CURRENT_TIMESTAMP),
    (UNHEX('f2222222222222222222222222222202'), UNHEX('e2222222222222222222222222222222'), 'APP-HD-ORG-BLK-L', 'Organic Cotton Oversized Hoodie', 78.00, 1, 78.00, CURRENT_TIMESTAMP),

    -- Items for Order 3
    (UNHEX('f3333333333333333333333333333301'), UNHEX('e3333333333333333333333333333333'), 'KIT-ESP-SMART-SS', 'Smart WiFi Espresso Machine', 499.00, 1, 499.00, CURRENT_TIMESTAMP),
    (UNHEX('f3333333333333333333333333333302'), UNHEX('e3333333333333333333333333333333'), 'BK-DDIA-KLIPP-PB', 'Designing Data-Intensive Applications', 45.99, 1, 45.99, CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE product_name = VALUES(product_name);
