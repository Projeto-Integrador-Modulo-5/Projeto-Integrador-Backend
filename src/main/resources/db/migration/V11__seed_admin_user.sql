INSERT INTO users (id, name, email, password_hash, role, created_at)
VALUES (
    gen_random_uuid(),
    'System Admin',
    'sysadmin@admin.com',
    '$2b$10$Y4i8NY/0Vc6ZrPgAEg9flu2WB7LQjV./TDTbSB99MoKtjPokJ.Bgi',
    'ADMIN',
    NOW()
)
ON CONFLICT (email) DO NOTHING;
