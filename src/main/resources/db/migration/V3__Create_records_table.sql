CREATE TABLE records (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    operation_id CHAR(36) NOT NULL,
    user_id CHAR(36) NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    user_balance DECIMAL(19,2) NOT NULL,
    operation_response TEXT NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_records_operation FOREIGN KEY (operation_id) REFERENCES operations(id),
    CONSTRAINT fk_records_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_records_user_id ON records(user_id);
CREATE INDEX idx_records_operation_id ON records(operation_id);
CREATE INDEX idx_records_created_at ON records(created_at);
CREATE INDEX idx_records_deleted ON records(deleted);
CREATE INDEX idx_records_user_deleted ON records(user_id, deleted);
