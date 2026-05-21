-- 1. Remove o DEFAULT que ainda referencia o tipo user_role
ALTER TABLE users ALTER COLUMN role DROP DEFAULT;

-- 2. Converte a coluna para VARCHAR (Hibernate 6 envia string, não o tipo nativo)
ALTER TABLE users ALTER COLUMN role TYPE VARCHAR(50);

-- 3. Agora o tipo não tem mais dependentes — pode ser dropado
DROP TYPE IF EXISTS user_role;

-- 4. Restaura o DEFAULT como string simples
ALTER TABLE users ALTER COLUMN role SET DEFAULT 'CUSTOMER';
