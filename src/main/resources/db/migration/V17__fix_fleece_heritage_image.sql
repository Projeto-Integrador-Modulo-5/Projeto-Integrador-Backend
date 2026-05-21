-- V17: Corrige imagem errada da Blusa de Frio Fleece Heritage (era uma bolsa lol)
UPDATE products
SET image_url = 'https://images.unsplash.com/photo-1509921463429-d3092d2582f8?w=600&q=80'
WHERE name = 'Blusa de Frio Fleece Heritage';
