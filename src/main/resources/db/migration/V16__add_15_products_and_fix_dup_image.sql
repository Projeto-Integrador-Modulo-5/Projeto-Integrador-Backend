-- =============================================
-- V16: +15 produtos (Social, Regata, Jaqueta, Blusa de Frio)
--      + corrige imagem duplicada do Dry-Fit Performance
-- =============================================

-- Corrige foto duplicada entre Dry-Fit e Running Ultra-Light
UPDATE products
SET image_url = 'https://images.unsplash.com/photo-1517466787929-bc90951d0974?w=600&q=80'
WHERE name = 'Camiseta Dry-Fit Performance';

DO $$
DECLARE
    -- Social
    s1 UUID := gen_random_uuid();
    s2 UUID := gen_random_uuid();
    s3 UUID := gen_random_uuid();
    s4 UUID := gen_random_uuid();
    -- Regata
    r1 UUID := gen_random_uuid();
    r2 UUID := gen_random_uuid();
    r3 UUID := gen_random_uuid();
    r4 UUID := gen_random_uuid();
    -- Jaqueta
    j1 UUID := gen_random_uuid();
    j2 UUID := gen_random_uuid();
    j3 UUID := gen_random_uuid();
    j4 UUID := gen_random_uuid();
    -- Blusa de Frio
    b1 UUID := gen_random_uuid();
    b2 UUID := gen_random_uuid();
    b3 UUID := gen_random_uuid();
BEGIN

-- ═══════════════════════════════════════
-- CAMISETAS SOCIAIS
-- ═══════════════════════════════════════

INSERT INTO products (id, name, description, price, image_url, category, active) VALUES (
    s1, 'Camiseta Social Slim Branca',
    'Tecido plano leve com caimento slim fit. Ideal para looks casuais ou formais com calça social.',
    159.90,
    'https://images.unsplash.com/photo-1607345366928-199ea26cfe3e?w=600&q=80',
    'Social', TRUE
);
INSERT INTO product_sizes (product_id, size, stock_quantity) VALUES
    (s1,'P',14),(s1,'M',22),(s1,'G',18),(s1,'GG',9);

INSERT INTO products (id, name, description, price, image_url, category, active) VALUES (
    s2, 'Camiseta Social Oxford Azul',
    'Tecido Oxford respirável em azul clássico. Gola italiana com botões discretos.',
    169.90,
    'https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=600&q=80',
    'Social', TRUE
);
INSERT INTO product_sizes (product_id, size, stock_quantity) VALUES
    (s2,'P',10),(s2,'M',20),(s2,'G',16),(s2,'GG',7);

INSERT INTO products (id, name, description, price, image_url, category, active) VALUES (
    s3, 'Camiseta Social Linho Natural',
    'Blend de linho com algodão para dias quentes. Textura suave, caimento leve e respirável.',
    179.90,
    'https://images.unsplash.com/photo-1603252109303-2751441dd157?w=600&q=80',
    'Social', TRUE
);
INSERT INTO product_sizes (product_id, size, stock_quantity) VALUES
    (s3,'P',8),(s3,'M',16),(s3,'G',12),(s3,'GG',5);

INSERT INTO products (id, name, description, price, image_url, category, active) VALUES (
    s4, 'Camiseta Social Preta Slim',
    'Cor preta atemporal em tecido plano fino. Corte slim que valoriza a silhueta.',
    149.90,
    'https://images.unsplash.com/photo-1604695573706-53170668f6a6?w=600&q=80',
    'Social', TRUE
);
INSERT INTO product_sizes (product_id, size, stock_quantity) VALUES
    (s4,'P',12),(s4,'M',24),(s4,'G',18),(s4,'GG',8);

-- ═══════════════════════════════════════
-- REGATAS
-- ═══════════════════════════════════════

INSERT INTO products (id, name, description, price, image_url, category, active) VALUES (
    r1, 'Regata Essential Cotton',
    'Regata básica em algodão 100% puro. Perfeita para o dia a dia ou como base.',
    59.90,
    'https://images.unsplash.com/photo-1540497077202-7c8a3999166f?w=600&q=80',
    'Active', TRUE
);
INSERT INTO product_sizes (product_id, size, stock_quantity) VALUES
    (r1,'P',20),(r1,'M',35),(r1,'G',28),(r1,'GG',12);

INSERT INTO products (id, name, description, price, image_url, category, active) VALUES (
    r2, 'Regata Dry-Fit Training',
    'Tecido Dry-Fit com tecnologia de evaporação rápida. Ideal para treinos intensos.',
    79.90,
    'https://images.unsplash.com/photo-1534438327276-14e5300c3a48?w=600&q=80',
    'Active', TRUE
);
INSERT INTO product_sizes (product_id, size, stock_quantity) VALUES
    (r2,'P',18),(r2,'M',30),(r2,'G',24),(r2,'GG',10);

INSERT INTO products (id, name, description, price, image_url, category, active) VALUES (
    r3, 'Regata Urban Oversized',
    'Regata ampla com caimento oversized para o estilo urbano relaxado.',
    69.90,
    'https://images.unsplash.com/photo-1506629082955-511b1aa562c8?w=600&q=80',
    'Active', TRUE
);
INSERT INTO product_sizes (product_id, size, stock_quantity) VALUES
    (r3,'P',15),(r3,'M',25),(r3,'G',20),(r3,'GG',8);

INSERT INTO products (id, name, description, price, image_url, category, active) VALUES (
    r4, 'Regata Muscle Fit Premium',
    'Corte muscle para valorizar o físico. Algodão penteado de alta gramatura.',
    74.90,
    'https://images.unsplash.com/photo-1571902943202-507ec2618e8f?w=600&q=80',
    'Active', TRUE
);
INSERT INTO product_sizes (product_id, size, stock_quantity) VALUES
    (r4,'P',12),(r4,'M',22),(r4,'G',18),(r4,'GG',7);

-- ═══════════════════════════════════════
-- JAQUETAS
-- ═══════════════════════════════════════

INSERT INTO products (id, name, description, price, image_url, category, active) VALUES (
    j1, 'Jaqueta Bomber Clássica',
    'Jaqueta bomber com ribana nas barras e gola. Forro interno aveludado, bolsos laterais.',
    329.90,
    'https://images.unsplash.com/photo-1551028719-00167b16eac5?w=600&q=80',
    'Outerwear', TRUE
);
INSERT INTO product_sizes (product_id, size, stock_quantity) VALUES
    (j1,'P',8),(j1,'M',14),(j1,'G',12),(j1,'GG',5);

INSERT INTO products (id, name, description, price, image_url, category, active) VALUES (
    j2, 'Jaqueta Corta-Vento Técnica',
    'Shell leve impermeável com capuz embutido. Proteção contra vento e garoa.',
    269.90,
    'https://images.unsplash.com/photo-1591047139829-d91aecb6caea?w=600&q=80',
    'Outerwear', TRUE
);
INSERT INTO product_sizes (product_id, size, stock_quantity) VALUES
    (j2,'P',10),(j2,'M',18),(j2,'G',14),(j2,'GG',6);

INSERT INTO products (id, name, description, price, image_url, category, active) VALUES (
    j3, 'Jaqueta Jeans Oversized',
    'Denim pesado com lavagem stonewashed. Caimento oversized, botões de metal envelhecido.',
    359.90,
    'https://images.unsplash.com/photo-1576871337632-b9aef4c17ab9?w=600&q=80',
    'Outerwear', TRUE
);
INSERT INTO product_sizes (product_id, size, stock_quantity) VALUES
    (j3,'P',7),(j3,'M',12),(j3,'G',10),(j3,'GG',4);

INSERT INTO products (id, name, description, price, image_url, category, active) VALUES (
    j4, 'Jaqueta Softshell Urbana',
    'Softshell stretch de 3 camadas com forro micro-polar. Conforto térmico para o cotidiano.',
    299.90,
    'https://images.unsplash.com/photo-1548624313-0396c75e4b1a?w=600&q=80',
    'Outerwear', TRUE
);
INSERT INTO product_sizes (product_id, size, stock_quantity) VALUES
    (j4,'P',9),(j4,'M',16),(j4,'G',13),(j4,'GG',5);

-- ═══════════════════════════════════════
-- BLUSAS DE FRIO
-- ═══════════════════════════════════════

INSERT INTO products (id, name, description, price, image_url, category, active) VALUES (
    b1, 'Moletom Canguru Premium',
    'Moletom 100% algodão com bolso canguru e capuz forrado. Toque macio e gramatura 320g.',
    249.90,
    'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?w=600&q=80',
    'Casual', TRUE
);
INSERT INTO product_sizes (product_id, size, stock_quantity) VALUES
    (b1,'P',10),(b1,'M',20),(b1,'G',16),(b1,'GG',7);

INSERT INTO products (id, name, description, price, image_url, category, active) VALUES (
    b2, 'Moletom Crewneck Essential',
    'Crewneck em french terry de algodão. Caimento relaxado, punhos e barra com ribana.',
    219.90,
    'https://images.unsplash.com/photo-1565693413579-8ff3fdc1b03b?w=600&q=80',
    'Casual', TRUE
);
INSERT INTO product_sizes (product_id, size, stock_quantity) VALUES
    (b2,'P',12),(b2,'M',22),(b2,'G',18),(b2,'GG',8);

INSERT INTO products (id, name, description, price, image_url, category, active) VALUES (
    b3, 'Blusa de Frio Fleece Heritage',
    'Fleece de dupla face ultra-macio. Design minimalista com logo bordado no peito.',
    199.90,
    'https://images.unsplash.com/photo-1591561954557-26941169b49e?w=600&q=80',
    'Casual', TRUE
);
INSERT INTO product_sizes (product_id, size, stock_quantity) VALUES
    (b3,'P',11),(b3,'M',21),(b3,'G',17),(b3,'GG',7);

END $$;
