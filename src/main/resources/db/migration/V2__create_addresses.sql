CREATE TABLE addresses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    street VARCHAR(255) NOT NULL,
    number VARCHAR(50) NOT NULL,
    complement VARCHAR(255),
    neighborhood VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    state CHAR(2) NOT NULL,
    zip_code VARCHAR(20) NOT NULL,
    is_default BOOLEAN NOT NULL DEFAULT FALSE
);
