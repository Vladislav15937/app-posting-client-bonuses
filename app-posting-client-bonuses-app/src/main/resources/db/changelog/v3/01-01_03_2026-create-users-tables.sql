-- liquibase formatted sql

-- ===================================================
-- changeset inzhevatkin.v:3-1-1
-- comment: Создание таблицы пользователей
-- ===================================================
CREATE TABLE IF NOT EXISTS bonus_system.users (
                                                  id BIGSERIAL PRIMARY KEY,
                                                  username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE,
    client_id VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

COMMENT ON TABLE bonus_system.users IS 'Пользователи системы';
COMMENT ON COLUMN bonus_system.users.username IS 'Уникальное имя пользователя для входа';
COMMENT ON COLUMN bonus_system.users.password IS 'Хеш пароля (BCrypt)';
COMMENT ON COLUMN bonus_system.users.email IS 'Email пользователя';
COMMENT ON COLUMN bonus_system.users.client_id IS 'ID клиента, если пользователь привязан к клиенту';

-- ===================================================
-- changeset inzhevatkin.v:3-1-2
-- comment: Создание таблицы ролей
-- ===================================================
CREATE TABLE IF NOT EXISTS bonus_system.roles (
                                                  id BIGSERIAL PRIMARY KEY,
                                                  name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200)
    );

COMMENT ON TABLE bonus_system.roles IS 'Роли пользователей';
COMMENT ON COLUMN bonus_system.roles.name IS 'Название роли (например, BONUS_READ, BONUS_WRITE)';
COMMENT ON COLUMN bonus_system.roles.description IS 'Описание роли';

-- ===================================================
-- changeset inzhevatkin.v:3-1-3
-- comment: Создание таблицы связей пользователей и ролей
-- ===================================================
CREATE TABLE IF NOT EXISTS bonus_system.user_roles (
                                                       user_id BIGINT NOT NULL,
                                                       role_id BIGINT NOT NULL,
                                                       PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES bonus_system.users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES bonus_system.roles(id) ON DELETE CASCADE
    );

COMMENT ON TABLE bonus_system.user_roles IS 'Связь пользователей с ролями';
COMMENT ON COLUMN bonus_system.user_roles.user_id IS 'ID пользователя';
COMMENT ON COLUMN bonus_system.user_roles.role_id IS 'ID роли';

-- ===================================================
-- changeset inzhevatkin.v:3-1-4
-- comment: Создание индексов для производительности
-- ===================================================
CREATE INDEX IF NOT EXISTS idx_users_username ON bonus_system.users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON bonus_system.users(email);
CREATE INDEX IF NOT EXISTS idx_user_roles_user_id ON bonus_system.user_roles(user_id);
CREATE INDEX IF NOT EXISTS idx_user_roles_role_id ON bonus_system.user_roles(role_id);