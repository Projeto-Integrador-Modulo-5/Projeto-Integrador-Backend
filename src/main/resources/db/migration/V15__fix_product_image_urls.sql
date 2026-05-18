-- =============================================
-- V15: Corrige image_url dos 6 novos produtos
-- =============================================

UPDATE products
SET image_url = 'https://images.unsplash.com/photo-1529374255404-311a2a4f1fd9?w=600&q=80'
WHERE name = 'Camiseta Box Logo Premium';

UPDATE products
SET image_url = 'https://images.unsplash.com/photo-1503341455253-b2e723bb3dbb?w=600&q=80'
WHERE name = 'Camiseta Raglan Contrast';

UPDATE products
SET image_url = 'https://images.unsplash.com/photo-1523381210434-271e8be1f52b?w=600&q=80'
WHERE name = 'Camiseta Tie-Dye Summer';

UPDATE products
SET image_url = 'https://images.unsplash.com/photo-1489987707025-afc232f7ea0f?w=600&q=80'
WHERE name = 'Camiseta Polo Piquet';

UPDATE products
SET image_url = 'https://images.unsplash.com/photo-1581655353564-df123a1eb820?w=600&q=80'
WHERE name = 'Camiseta Manga Longa Thermal';

UPDATE products
SET image_url = 'https://images.unsplash.com/photo-1571945153237-4929e783af4a?w=600&q=80'
WHERE name = 'Camiseta Running Ultra-Light';
