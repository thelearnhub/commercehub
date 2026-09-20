-- Seed Dummy Notification Logs for Notification Service

INSERT INTO notification_logs (id, recipient, channel, subject, content, status, error_message, created_at)
VALUES
    (1, 'customer@commercehub.com', 'EMAIL', 'Order Confirmation #e1111111', 'Thank you for your order! Your MacBook Pro 16" and Sony Headphones are on their way.', 'SENT', NULL, CURRENT_TIMESTAMP),

    (2, 'john.doe@example.com', 'SMS', NULL, 'CommerceHub: Your order #e2222222 has shipped! Track with UPS: TRK-UPS-1234567890', 'SENT', NULL, CURRENT_TIMESTAMP),

    (3, 'jane.smith@example.com', 'PUSH', 'Payment Received', 'We have received your payment of $544.99 for order #e3333333.', 'SENT', NULL, CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE status = VALUES(status);
