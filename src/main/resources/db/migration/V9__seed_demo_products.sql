-- =============================================
-- DEMO PRODUCTS SEED
-- =============================================

DO $$
DECLARE
    p1 UUID := gen_random_uuid();
    p2 UUID := gen_random_uuid();
    p3 UUID := gen_random_uuid();
    p4 UUID := gen_random_uuid();
    p5 UUID := gen_random_uuid();
    p6 UUID := gen_random_uuid();
    p7 UUID := gen_random_uuid();
    p8 UUID := gen_random_uuid();
BEGIN

-- ── Produto 1 ──────────────────────────────────────────────────────────────
INSERT INTO products (id, name, description, price, image_url, active)
VALUES (
    p1,
    'Camiseta Classic Black',
    'Camiseta 100% algodão premium, corte regular, perfeita para o dia a dia.',
    89.90,
    'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=600&q=80',
    TRUE
);
INSERT INTO product_sizes (product_id, size, stock_quantity) VALUES
    (p1, 'P',  15),
    (p1, 'M',  30),
    (p1, 'G',  25),
    (p1, 'GG', 10);

-- ── Produto 2 ──────────────────────────────────────────────────────────────
INSERT INTO products (id, name, description, price, image_url, active)
VALUES (
    p2,
    'Camiseta Urban White',
    'Camiseta branca de algodão penteado, caimento slim, ideal para looks minimalistas.',
    79.90,
    'https://images.unsplash.com/photo-1622445275463-afa2ab738c34?w=600&q=80',
    TRUE
);
INSERT INTO product_sizes (product_id, size, stock_quantity) VALUES
    (p2, 'P',  20),
    (p2, 'M',  35),
    (p2, 'G',  20),
    (p2, 'GG', 8);

-- ── Produto 3 ──────────────────────────────────────────────────────────────
INSERT INTO products (id, name, description, price, image_url, active)
VALUES (
    p3,
    'Camiseta Vintage Wash',
    'Acabamento estonado que garante um visual retrô autêntico. Algodão macio.',
    99.90,
    'https://images.unsplash.com/photo-1503341504253-dff4815485f1?w=600&q=80',
    TRUE
);
INSERT INTO product_sizes (product_id, size, stock_quantity) VALUES
    (p3, 'P',  12),
    (p3, 'M',  22),
    (p3, 'G',  18),
    (p3, 'GG', 6);

-- ── Produto 4 ──────────────────────────────────────────────────────────────
INSERT INTO products (id, name, description, price, image_url, active)
VALUES (
    p4,
    'Camiseta Graphic Tee – Waves',
    'Estampa exclusiva de ondas em serigrafia. 100% algodão, caimento regular.',
    109.90,
    'https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?w=600&q=80',
    TRUE
);
INSERT INTO product_sizes (product_id, size, stock_quantity) VALUES
    (p4, 'P',  10),
    (p4, 'M',  25),
    (p4, 'G',  20),
    (p4, 'GG', 5);

-- ── Produto 5 ──────────────────────────────────────────────────────────────
INSERT INTO products (id, name, description, price, image_url, active)
VALUES (
    p5,
    'Camiseta Oversize Navy',
    'Modelagem oversize com caimento relaxado. Algodão 280g, super encorpado.',
    119.90,
    'https://images.unsplash.com/photo-1618354691373-d851c5c3a990?w=600&q=80',
    TRUE
);
INSERT INTO product_sizes (product_id, size, stock_quantity) VALUES
    (p5, 'P',  8),
    (p5, 'M',  18),
    (p5, 'G',  22),
    (p5, 'GG', 12);

-- ── Produto 6 ──────────────────────────────────────────────────────────────
INSERT INTO products (id, name, description, price, image_url, active)
VALUES (
    p6,
    'Camiseta Dry-Fit Performance',
    'Tecido dry-fit com proteção UV50+. Ideal para treinos e atividades ao ar livre.',
    129.90,
    'https://images.unsplash.com/photo-1571945153237-4929e783af4a?w=600&q=80',
    TRUE
);
INSERT INTO product_sizes (product_id, size, stock_quantity) VALUES
    (p6, 'P',  14),
    (p6, 'M',  28),
    (p6, 'G',  22),
    (p6, 'GG', 9);

-- ── Produto 7 ──────────────────────────────────────────────────────────────
INSERT INTO products (id, name, description, price, image_url, active)
VALUES (
    p7,
    'Camiseta Listrada Breton',
    'Clássica listrada em algodão pima. Corte slim com gola careca reforçada.',
    94.90,
    'https://images.unsplash.com/photo-1604006852748-903fccbc4019?w=600&q=80',
    TRUE
);
INSERT INTO product_sizes (product_id, size, stock_quantity) VALUES
    (p7, 'P',  16),
    (p7, 'M',  24),
    (p7, 'G',  14),
    (p7, 'GG', 7);

-- ── Produto 8 ──────────────────────────────────────────────────────────────
INSERT INTO products (id, name, description, price, image_url, active)
VALUES (
    p8,
    'Camiseta Essential Grey',
    'Cinza mescla em algodão flame. Versátil e confortável para qualquer ocasião.',
    84.90,
    'https://images.unsplash.com/photo-1562157873-818bc0726f68?w=600&q=80',
    TRUE
);
INSERT INTO product_sizes (product_id, size, stock_quantity) VALUES
    (p8, 'P',  20),
    (p8, 'M',  32),
    (p8, 'G',  26),
    (p8, 'GG', 11);

END $$;
