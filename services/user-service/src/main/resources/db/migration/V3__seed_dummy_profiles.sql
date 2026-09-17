-- Seed Dummy Profiles and Addresses for User Service

INSERT INTO user_profiles (id, email, first_name, last_name, phone, avatar_url, created_at)
VALUES
    -- Admin Profile
    (UNHEX('11111111111111111111111111111111'), 'admin@commercehub.com', 'Alex', 'Administrator', '+1-800-555-0100', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150', CURRENT_TIMESTAMP),
    
    -- Seller Profile
    (UNHEX('22222222222222222222222222222222'), 'seller@commercehub.com', 'Sam', 'Seller', '+1-800-555-0200', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150', CURRENT_TIMESTAMP),
    
    -- Customer 1 Profile
    (UNHEX('33333333333333333333333333333333'), 'customer@commercehub.com', 'Charlie', 'Customer', '+1-800-555-0300', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150', CURRENT_TIMESTAMP),
    
    -- Customer 2 Profile
    (UNHEX('44444444444444444444444444444444'), 'john.doe@example.com', 'John', 'Doe', '+1-555-0199', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150', CURRENT_TIMESTAMP),
    
    -- Customer 3 Profile
    (UNHEX('55555555555555555555555555555555'), 'jane.smith@example.com', 'Jane', 'Smith', '+1-555-0188', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=150', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE email = VALUES(email);

INSERT INTO addresses (id, user_id, label, street, city, state, zip_code, country, is_default, created_at)
VALUES
    -- Admin Headquarters Address
    (UNHEX('11111111111111111111111111111101'), UNHEX('11111111111111111111111111111111'), 'Headquarters', '100 Commerce Way, Suite 500', 'San Francisco', 'CA', '94105', 'USA', TRUE, CURRENT_TIMESTAMP),

    -- Seller Store Address
    (UNHEX('22222222222222222222222222222201'), UNHEX('22222222222222222222222222222222'), 'TechHub Store Depot', '200 Merchant Blvd', 'Austin', 'TX', '78701', 'USA', TRUE, CURRENT_TIMESTAMP),

    -- Customer 1 Default Home Address
    (UNHEX('33333333333333333333333333333301'), UNHEX('33333333333333333333333333333333'), 'Home', '742 Evergreen Terrace', 'Springfield', 'OR', '97477', 'USA', TRUE, CURRENT_TIMESTAMP),

    -- Customer 2 Default Home Address
    (UNHEX('44444444444444444444444444444401'), UNHEX('44444444444444444444444444444444'), 'Apartment', '123 Main Street, Apt 4B', 'New York', 'NY', '10001', 'USA', TRUE, CURRENT_TIMESTAMP),

    -- Customer 3 Default Home Address
    (UNHEX('55555555555555555555555555555501'), UNHEX('55555555555555555555555555555555'), 'Home', '456 Oak Avenue', 'Seattle', 'WA', '98101', 'USA', TRUE, CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE label = VALUES(label);
