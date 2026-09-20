-- Seed Dummy Categories & Products for Product Service

INSERT INTO categories (id, name, description, created_at)
VALUES
    (UNHEX('a1111111111111111111111111111111'), 'Electronics', 'Laptops, smartphones, audio gear, and gaming peripherals', CURRENT_TIMESTAMP),
    (UNHEX('a2222222222222222222222222222222'), 'Fashion', 'Apparel, activewear, footwear, and accessories', CURRENT_TIMESTAMP),
    (UNHEX('a3333333333333333333333333333333'), 'Home & Living', 'Ergonomic furniture, decor, and smart appliances', CURRENT_TIMESTAMP),
    (UNHEX('a4444444444444444444444444444444'), 'Books & Media', 'Best-selling engineering books, software architecture, and e-readers', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO products (id, name, sku, description, base_price, stock_quantity, category_id, created_at)
VALUES
    -- Electronics
    (UNHEX('b1111111111111111111111111111111'), 'MacBook Pro 16" M3 Max', 'LAP-MBP-16-M3', 'Apple M3 Max chip with 16-core CPU and 40-core GPU, 36GB Unified Memory, 1TB SSD Storage, Liquid Retina XDR display.', 2499.99, 50, UNHEX('a1111111111111111111111111111111'), CURRENT_TIMESTAMP),

    (UNHEX('b2222222222222222222222222222222'), 'Sony WH-1000XM5 Wireless Headphones', 'AUD-SONY-XM5-BLK', 'Industry-leading noise canceling headphones with Auto NC Optimizer, 30-hour battery life, and crystal-clear hands-free calling.', 399.99, 120, UNHEX('a1111111111111111111111111111111'), CURRENT_TIMESTAMP),

    (UNHEX('b3333333333333333333333333333333'), 'Ultra-Wide Curved Gaming Monitor 34"', 'MON-34-UW-144HZ', '34-inch WQHD (3440 x 1440) 1500R Curved Display with 144Hz Refresh Rate, HDR400, and 1ms response time.', 649.50, 35, UNHEX('a1111111111111111111111111111111'), CURRENT_TIMESTAMP),

    -- Fashion
    (UNHEX('b4444444444444444444444444444444'), 'Nike Air Force 1 ''07 Sneaker', 'SHOE-NIKE-AF1-WHT-10', 'Classic crisp leather basketball icon featuring stitched overlays, clean finishes, and responsive Air cushioning.', 115.00, 200, UNHEX('a2222222222222222222222222222222'), CURRENT_TIMESTAMP),

    (UNHEX('b5555555555555555555555555555555'), 'Organic Cotton Oversized Hoodie', 'APP-HD-ORG-BLK-L', 'Heavyweight 450gsm 100% organic cotton fleece hoodie with double-lined hood and relaxed streetwear fit.', 78.00, 85, UNHEX('a2222222222222222222222222222222'), CURRENT_TIMESTAMP),

    -- Home & Living
    (UNHEX('b6666666666666666666666666666666'), 'Ergonomic Mesh Office Chair', 'FUR-CHR-ERG-GRY', 'Breathable mesh high-back executive desk chair with adjustable 3D lumbar support, armrests, and synchro-tilt recline.', 299.00, 40, UNHEX('a3333333333333333333333333333333'), CURRENT_TIMESTAMP),

    (UNHEX('b7777777777777777777777777777777'), 'Smart WiFi Espresso Machine', 'KIT-ESP-SMART-SS', '15-bar Italian pump espresso machine with integrated precision conical burr grinder, automatic steam wand, and mobile app control.', 499.00, 60, UNHEX('a3333333333333333333333333333333'), CURRENT_TIMESTAMP),

    -- Books & Media
    (UNHEX('b8888888888888888888888888888888'), 'Designing Data-Intensive Applications', 'BK-DDIA-KLIPP-PB', 'The definitive guide to the architecture of modern data systems by Martin Kleppmann. Deep dive into distributed storage, replication, and consensus.', 45.99, 150, UNHEX('a4444444444444444444444444444444'), CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE name = VALUES(name);
