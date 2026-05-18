-- =============================================
-- V14: Adiciona coluna category + seed produtos
-- =============================================

-- 1. Adiciona coluna category (nullable para não quebrar registros existentes)
ALTER TABLE products ADD COLUMN IF NOT EXISTS category VARCHAR(100);

-- 2. Atualiza categorias dos produtos seedados pelo V9
UPDATE products SET category = 'Essentials' WHERE name = 'Camiseta Classic Black';
UPDATE products SET category = 'Essentials' WHERE name = 'Camiseta Urban White';
UPDATE products SET category = 'Heritage'   WHERE name = 'Camiseta Vintage Wash';
UPDATE products SET category = 'Streetwear' WHERE name = 'Camiseta Graphic Tee – Waves';
UPDATE products SET category = 'Streetwear' WHERE name = 'Camiseta Oversize Navy';
UPDATE products SET category = 'Sport'      WHERE name = 'Camiseta Dry-Fit Performance';
UPDATE products SET category = 'Heritage'   WHERE name = 'Camiseta Listrada Breton';
UPDATE products SET category = 'Essentials' WHERE name = 'Camiseta Essential Grey';

-- 3. Insere 6 novos produtos
DO $$
DECLARE
    p9  UUID := gen_random_uuid();
    p10 UUID := gen_random_uuid();
    p11 UUID := gen_random_uuid();
    p12 UUID := gen_random_uuid();
    p13 UUID := gen_random_uuid();
    p14 UUID := gen_random_uuid();
BEGIN

-- ── Produto 9 — Limited ───────────────────────────────────────────────────────
INSERT INTO products (id, name, description, price, image_url, category, active)
VALUES (
    p9,
    'Camiseta Box Logo Premium',
    'Edição limitada com logo bordado em relevo. Algodão 300g, caimento oversized.',
    189.90,
    'https://images.unsplash.com/photo-1576566588028-4147f3842f27?w=600&q=80',
    'Limited',
    TRUE
);
INSERT INTO product_sizes (product_id, size, stock_quantity) VALUES
    (p9, 'P',  5),
    (p9, 'M',  8),
    (p9, 'G',  6),
    (p9, 'GG', 3);

-- ── Produto 10 — Streetwear ───────────────────────────────────────────────────
INSERT INTO products (id, name, description, price, image_url, category, active)
VALUES (
    p10,
    'Camiseta Raglan Contrast',
    'Modelo raglan com mangas em cor contrastante. Algodão penteado, caimento regular.',
    99.90,
    'https://images.unsplash.com/photo-1618354691438-25bc04584c23?w=600&q=80',
    'Streetwear',
    TRUE
);
INSERT INTO product_sizes (product_id, size, stock_quantity) VALUES
    (p10, 'P',  12),
    (p10, 'M',  20),
    (p10, 'G',  16),
    (p10, 'GG', 7);

-- ── Produto 11 — Streetwear ───────────────────────────────────────────────────
INSERT INTO products (id, name, description, price, image_url, category, active)
VALUES (
    p11,
    'Camiseta Tie-Dye Summer',
    'Estamparia artesanal tie-dye com cores vibrantes. Algodão macio 160g.',
    114.90,
    'https://images.unsplash.com/photo-1556821840-3a63f15732ce?w=600&q=80',
    'Streetwear',
    TRUE
);
INSERT INTO product_sizes (product_id, size, stock_quantity) VALUES
    (p11, 'P',  9),
    (p11, 'M',  18),
    (p11, 'G',  14),
    (p11, 'GG', 5);

-- ── Produto 12 — Essentials ───────────────────────────────────────────────────
INSERT INTO products (id, name, description, price, image_url, category, active)
VALUES (
    p12,
    'Camiseta Polo Piquet',
    'Piquet de algodão com gola polo e botões de madrepérola. Elegância casual.',
    139.90,
    'https://images.unsplash.com/photo-1586790170083-2f9ceadc732d?w=600&q=80',
    'Essentials',
    TRUE
);
INSERT INTO product_sizes (product_id, size, stock_quantity) VALUES
    (p12, 'P',  10),
    (p12, 'M',  22),
    (p12, 'G',  18),
    (p12, 'GG', 8);

-- ── Produto 13 — Heritage ─────────────────────────────────────────────────────
INSERT INTO products (id, name, description, price, image_url, category, active)
VALUES (
    p13,
    'Camiseta Manga Longa Thermal',
    'Manga longa em malha thermal de algodão com elastano. Conforto para dias frios.',
    124.90,
    'https://images.unsplash.com/photo-1620799140408-edc6dcb6d633?w=600&q=80',
    'Heritage',
    TRUE
);
INSERT INTO product_sizes (product_id, size, stock_quantity) VALUES
    (p13, 'P',  8),
    (p13, 'M',  15),
    (p13, 'G',  12),
    (p13, 'GG', 6);

-- ── Produto 14 — Sport ────────────────────────────────────────────────────────
INSERT INTO products (id, name, description, price, image_url, category, active)
VALUES (
    p14,
    'Camiseta Running Ultra-Light',
    'Tecido ultra-leve com ventilação estratégica. Ideal para corrida e crossfit.',
    144.90,
    'https://images.unsplash.com/photo-1552674605-db6ffd4facb5?w=600&q=80',
    'Sport',
    TRUE
);
INSERT INTO product_sizes (product_id, size, stock_quantity) VALUES
    (p14, 'P',  11),
    (p14, 'M',  24),
    (p14, 'G',  19),
    (p14, 'GG', 8);

END $$;
