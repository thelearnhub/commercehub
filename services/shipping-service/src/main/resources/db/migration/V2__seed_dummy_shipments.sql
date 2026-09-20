-- Seed Dummy Shipments & Status History for Shipping Service

INSERT INTO shipments (id, order_id, tracking_number, carrier, status, recipient_name, shipping_address, estimated_delivery_date, created_at)
VALUES
    (1, 'e1111111-1111-1111-1111-111111111111', 'TRK-FEDEX-9876543210', 'FEDEX', 'DELIVERED', 'Charlie Customer', '742 Evergreen Terrace, Springfield, OR 97477, USA', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    (2, 'e2222222-2222-2222-2222-222222222222', 'TRK-UPS-1234567890', 'UPS', 'IN_TRANSIT', 'John Doe', '123 Main Street, Apt 4B, New York, NY 10001, USA', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    (3, 'e3333333-3333-3333-3333-333333333333', 'TRK-USPS-5554443332', 'USPS', 'LABEL_CREATED', 'Jane Smith', '456 Oak Avenue, Seattle, WA 98101, USA', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE status = VALUES(status);

INSERT INTO shipment_status_history (id, shipment_id, status, comment, created_at)
VALUES
    (1, 1, 'LABEL_CREATED', 'Shipping label created for FedEx Express', CURRENT_TIMESTAMP),
    (2, 1, 'IN_TRANSIT', 'Package scanned at FedEx Hub', CURRENT_TIMESTAMP),
    (3, 1, 'DELIVERED', 'Delivered at front porch', CURRENT_TIMESTAMP),
    (4, 2, 'LABEL_CREATED', 'Shipping label created for UPS Ground', CURRENT_TIMESTAMP),
    (5, 2, 'IN_TRANSIT', 'Arrived at sorting facility', CURRENT_TIMESTAMP),
    (6, 3, 'LABEL_CREATED', 'Shipping label created for USPS Priority Mail', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE comment = VALUES(comment);
