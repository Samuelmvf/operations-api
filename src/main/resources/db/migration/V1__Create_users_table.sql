CREATE TABLE users (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    balance DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_deleted ON users(deleted);

INSERT INTO users (username, password, status, balance, deleted, created_at, updated_at) VALUES
    ('admin@admin.com', '$2a$10$m8dN7iBXnWagZnCT6Oy6iOzluv6U8SmCk2O5XkL.6GIWpWpftiBmC', 'ACTIVE', 250.00, FALSE, NOW(), NOW());
