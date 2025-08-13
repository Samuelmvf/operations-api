INSERT INTO operations (type, cost) VALUES
    ('ADDITION', 1.00),
    ('SUBTRACTION', 1.00),
    ('MULTIPLICATION', 2.00),
    ('DIVISION', 2.00),
    ('SQUARE_ROOT', 3.00),
    ('RANDOM_STRING', 5.00);

INSERT INTO users (username, password, status, balance, deleted, created_at, updated_at) VALUES
    ('admin@admin.com', '$2a$10$m8dN7iBXnWagZnCT6Oy6iOzluv6U8SmCk2O5XkL.6GIWpWpftiBmC', 'ACTIVE', 250.00, FALSE, NOW(), NOW());