-- ============================================================
-- V13 — Dados fictícios para demonstração
--        3 clientes + endereços + pedidos em status variados
-- ============================================================

DO $$
DECLARE
    -- Usuários
    u_ana     UUID := '11111111-0000-0000-0000-000000000001';
    u_carlos  UUID := '11111111-0000-0000-0000-000000000002';
    u_beatriz UUID := '11111111-0000-0000-0000-000000000003';

    -- Endereços
    addr_ana     UUID := '22222222-0000-0000-0000-000000000001';
    addr_carlos  UUID := '22222222-0000-0000-0000-000000000002';
    addr_beatriz UUID := '22222222-0000-0000-0000-000000000003';

    -- Pedidos
    ord1 UUID := '33333333-0000-0000-0000-000000000001';
    ord2 UUID := '33333333-0000-0000-0000-000000000002';
    ord3 UUID := '33333333-0000-0000-0000-000000000003';
    ord4 UUID := '33333333-0000-0000-0000-000000000004';
    ord5 UUID := '33333333-0000-0000-0000-000000000005';

    -- Produtos (buscados pelo nome para não depender do UUID gerado no V9)
    p_black   UUID;
    p_white   UUID;
    p_vintage UUID;
    p_waves   UUID;
    p_grey    UUID;
BEGIN

    -- Resolve IDs dos produtos
    SELECT id INTO p_black   FROM products WHERE name = 'Camiseta Classic Black'     LIMIT 1;
    SELECT id INTO p_white   FROM products WHERE name = 'Camiseta Urban White'        LIMIT 1;
    SELECT id INTO p_vintage FROM products WHERE name = 'Camiseta Vintage Wash'       LIMIT 1;
    SELECT id INTO p_waves   FROM products WHERE name = 'Camiseta Graphic Tee – Waves' LIMIT 1;
    SELECT id INTO p_grey    FROM products WHERE name = 'Camiseta Essential Grey'     LIMIT 1;

    -- ── Usuários ────────────────────────────────────────────────

    INSERT INTO users (id, name, email, password_hash, role, created_at) VALUES
        (u_ana,     'Ana Costa',      'ana.costa@email.com',
         '$2b$10$JVb3dhGtJYfBVX6gaueYQOd0xKPi7f4jMN2.LKVDe8G.3ssl1StCy', -- cliente123
         'CUSTOMER', NOW() - INTERVAL '30 days'),
        (u_carlos,  'Carlos Lima',    'carlos.lima@email.com',
         '$2b$10$ZMmZE5Cl7GWYqGiDENGUx.xNmhHNFPKJbu9xJ0F3cz7BzQyd6wXGy', -- maria123
         'CUSTOMER', NOW() - INTERVAL '20 days'),
        (u_beatriz, 'Beatriz Souza',  'beatriz.souza@email.com',
         '$2b$10$HJvWUBgffAM9jZTcgYnGNO8lUO/jB1yXM1I6fycjUR.csyE.gjtqa', -- joao123
         'CUSTOMER', NOW() - INTERVAL '10 days')
    ON CONFLICT (email) DO NOTHING;

    -- ── Endereços ───────────────────────────────────────────────

    INSERT INTO addresses (id, user_id, street, number, complement, neighborhood, city, state, zip_code, is_default) VALUES
        (addr_ana,     u_ana,     'Rua das Flores',     '123', 'Apto 42', 'Centro',         'São Paulo', 'SP', '01310-100', TRUE),
        (addr_carlos,  u_carlos,  'Av. Beira Mar',      '456', NULL,      'Praia do Canto', 'Vitória',   'ES', '29055-130', TRUE),
        (addr_beatriz, u_beatriz, 'Rua XV de Novembro', '789', 'Sala 3',  'Centro',         'Curitiba',  'PR', '80020-310', TRUE)
    ON CONFLICT DO NOTHING;

    -- ── Pedidos ─────────────────────────────────────────────────

    -- Pedido 1 — Ana, PROCESSING (recém criado)
    INSERT INTO orders (id, user_id, status, total, tracking_code, address_id, created_at, updated_at) VALUES
        (ord1, u_ana, 'PROCESSING', 179.80, NULL, addr_ana, NOW() - INTERVAL '2 hours', NOW() - INTERVAL '2 hours');
    INSERT INTO order_items (order_id, product_id, size, quantity, unit_price) VALUES
        (ord1, p_black, 'M', 2, 89.90);

    -- Pedido 2 — Ana, SHIPPED (em trânsito)
    INSERT INTO orders (id, user_id, status, total, tracking_code, address_id, created_at, updated_at) VALUES
        (ord2, u_ana, 'SHIPPED', 199.80, 'BR123456789SP', addr_ana, NOW() - INTERVAL '5 days', NOW() - INTERVAL '3 days');
    INSERT INTO order_items (order_id, product_id, size, quantity, unit_price) VALUES
        (ord2, p_white,   'P', 1, 79.90),
        (ord2, p_vintage, 'P', 1, 99.90)
        ;

    -- Pedido 3 — Carlos, PROCESSING
    INSERT INTO orders (id, user_id, status, total, tracking_code, address_id, created_at, updated_at) VALUES
        (ord3, u_carlos, 'PROCESSING', 219.80, NULL, addr_carlos, NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day');
    INSERT INTO order_items (order_id, product_id, size, quantity, unit_price) VALUES
        (ord3, p_waves, 'G', 2, 109.90);

    -- Pedido 4 — Carlos, DELIVERED (concluído)
    INSERT INTO orders (id, user_id, status, total, tracking_code, address_id, created_at, updated_at) VALUES
        (ord4, u_carlos, 'DELIVERED', 84.90, 'BR987654321ES', addr_carlos, NOW() - INTERVAL '15 days', NOW() - INTERVAL '10 days');
    INSERT INTO order_items (order_id, product_id, size, quantity, unit_price) VALUES
        (ord4, p_grey, 'GG', 1, 84.90);

    -- Pedido 5 — Beatriz, PROCESSING (mais recente, ótimo para demo ao vivo)
    INSERT INTO orders (id, user_id, status, total, tracking_code, address_id, created_at, updated_at) VALUES
        (ord5, u_beatriz, 'PROCESSING', 329.70, NULL, addr_beatriz, NOW() - INTERVAL '30 minutes', NOW() - INTERVAL '30 minutes');
    INSERT INTO order_items (order_id, product_id, size, quantity, unit_price) VALUES
        (ord5, p_black,   'M', 2, 89.90),
        (ord5, p_vintage, 'G', 1, 99.90)
        ;

END $$;
