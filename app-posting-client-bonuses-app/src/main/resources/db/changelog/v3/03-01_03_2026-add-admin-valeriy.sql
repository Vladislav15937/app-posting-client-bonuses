-- liquibase formatted sql

-- ===================================================
-- changeset inzhevatkin.v:3-3-1
-- comment: Добавление администратора Валерия
-- ===================================================
-- пароль: password
INSERT INTO bonus_system.users (username, password, email, client_id)
SELECT 'valeriy', '$2a$12$mSzoiYAM2JwpA3OcErYWZ.SUv6PS.8sfUdH6DCyb8.YQMui93SFOC',
       'valeriy@company.ru', 'admin_client'
    WHERE NOT EXISTS (SELECT 1 FROM bonus_system.users WHERE username = 'valeriy');

-- ===================================================
-- changeset inzhevatkin.v:3-3-2
-- comment: Назначение роли BONUS_ADMIN Валерию
-- ===================================================
INSERT INTO bonus_system.user_roles (user_id, role_id)
SELECT u.id, r.id
FROM bonus_system.users u, bonus_system.roles r
WHERE u.username = 'valeriy' AND r.name = 'BONUS_ADMIN'
    ON CONFLICT DO NOTHING;

-- ===================================================
-- changeset inzhevatkin.v:3-3-3
-- comment: Создаём пользователя с ролью BONUS_READ
-- ===================================================

-- Создаём пользователя с ролью BONUS_READ (Анна - менеджер по работе с клиентами)
-- пароль: 1
INSERT INTO bonus_system.users (username, password, email, client_id)
SELECT 'anna.reader', '$2a$12$wipHXoKZvigm6/woPWUiF.vI6Fn0HAvbxlC9s0phNFaW6jEAeBXnC',
       'anna.reader@company.ru', 'client_read_001'
    WHERE NOT EXISTS (SELECT 1 FROM bonus_system.users WHERE username = 'anna.reader');

-- Назначаем роль BONUS_READ Анне
INSERT INTO bonus_system.user_roles (user_id, role_id)
SELECT u.id, r.id
FROM bonus_system.users u, bonus_system.roles r
WHERE u.username = 'anna.reader' AND r.name = 'BONUS_READ'
    ON CONFLICT DO NOTHING;

-- ===================================================
-- changeset inzhevatkin.v:3-3-4
-- comment: Создаём пользователя с ролью BONUS_WRITE
-- ===================================================

-- Создаём пользователя с ролью BONUS_WRITE
-- пароль: 2
INSERT INTO bonus_system.users (username, password, email, client_id)
SELECT 'petr.writer', '$2a$12$mY21p6NH4clxxMvc/QgNqO2x8Q4KKsP5vFWKw/OOg.qS4Xr3STJrG',
       'petr.writer@company.ru', 'client_write_001'
    WHERE NOT EXISTS (SELECT 1 FROM bonus_system.users WHERE username = 'petr.writer');

-- Назначаем роль BONUS_WRITE Петру
INSERT INTO bonus_system.user_roles (user_id, role_id)
SELECT u.id, r.id
FROM bonus_system.users u, bonus_system.roles r
WHERE u.username = 'petr.writer' AND r.name = 'BONUS_WRITE'
    ON CONFLICT DO NOTHING;

-- Создаём пользователя с обеими ролями
-- пароль: 3
INSERT INTO bonus_system.users (username, password, email, client_id)
SELECT 'elena.both', '$2a$12$t1gGiuxDoRSuqWp0Dn4RF.ItYwm40OoKZ//NNeqMgB11KcTEbFD62',
       'elena.both@company.ru', 'client_both_001'
    WHERE NOT EXISTS (SELECT 1 FROM bonus_system.users WHERE username = 'elena.both');

-- Назначаем роль BONUS_READ Елене
INSERT INTO bonus_system.user_roles (user_id, role_id)
SELECT u.id, r.id
FROM bonus_system.users u, bonus_system.roles r
WHERE u.username = 'elena.both' AND r.name = 'BONUS_READ'
    ON CONFLICT DO NOTHING;

-- Назначаем роль BONUS_WRITE Елене
INSERT INTO bonus_system.user_roles (user_id, role_id)
SELECT u.id, r.id
FROM bonus_system.users u, bonus_system.roles r
WHERE u.username = 'elena.both' AND r.name = 'BONUS_WRITE'
    ON CONFLICT DO NOTHING;