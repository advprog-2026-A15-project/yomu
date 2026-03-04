CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(20) NOT NULL UNIQUE,
    display_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone_number VARCHAR(20) UNIQUE,
    password VARCHAR(100),
    role VARCHAR(10) NOT NULL,
    provider VARCHAR(10),
    provider_id VARCHAR(100),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);