CREATE TYPE order_status AS ENUM ('PROCESSING', 'SHIPPED', 'DELIVERED');

CREATE TABLE orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id),
    status order_status NOT NULL DEFAULT 'PROCESSING',
    total DECIMAL(10,2) NOT NULL,
    tracking_code VARCHAR(50),
    address_id UUID NOT NULL REFERENCES addresses(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
