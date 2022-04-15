CREATE TABLE member (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL,
    birth_date VARCHAR(8) NOT NULL,
    phone VARCHAR(11) NOT NULL UNIQUE,
    email VARCHAR(128) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id)
)

-- CREATE INDEX idx_member_email ON member (email);
-- CREATE INDEX idx_member_phone ON member (phone);