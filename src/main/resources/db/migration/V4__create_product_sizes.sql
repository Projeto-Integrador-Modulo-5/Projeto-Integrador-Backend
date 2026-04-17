CREATE TYPE product_size AS ENUM ('P', 'M', 'G', 'GG');

CREATE TABLE product_sizes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    size product_size NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    CONSTRAINT uq_product_size UNIQUE (product_id, size)
);
