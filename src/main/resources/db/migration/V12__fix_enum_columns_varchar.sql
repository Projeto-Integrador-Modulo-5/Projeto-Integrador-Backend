-- ============================================================
-- V12 — Converte colunas de enum nativo do PostgreSQL para
--        VARCHAR(50), compatível com Hibernate 6 @Enumerated(STRING)
-- ============================================================

-- ── 1. orders.status ────────────────────────────────────────
ALTER TABLE orders ALTER COLUMN status DROP DEFAULT;
ALTER TABLE orders ALTER COLUMN status TYPE VARCHAR(50)
    USING status::TEXT;
DROP TYPE IF EXISTS order_status;
ALTER TABLE orders ALTER COLUMN status SET DEFAULT 'PROCESSING';

-- ── 2. product_sizes.size ────────────────────────────────────
-- order_items.size referencia o mesmo tipo, deve ser convertido
-- antes de dropar o tipo
ALTER TABLE order_items  ALTER COLUMN size TYPE VARCHAR(10) USING size::TEXT;
ALTER TABLE product_sizes ALTER COLUMN size TYPE VARCHAR(10) USING size::TEXT;
DROP TYPE IF EXISTS product_size;

-- ── 3. notifications.type ────────────────────────────────────
ALTER TABLE notifications ALTER COLUMN type TYPE VARCHAR(50)
    USING type::TEXT;
DROP TYPE IF EXISTS notification_type;
