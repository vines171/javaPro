-- Вставляем тестовые данные только если таблица пустая
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM users LIMIT 1) THEN
        INSERT INTO users (username) VALUES
        ('alice'),
        ('bob'),
        ('charlie'),
        ('diana'),
        ('eve'),
        ('frank'),
        ('grace'),
        ('henry'),
        ('ivan'),
        ('julia');
    END IF;
END $$;