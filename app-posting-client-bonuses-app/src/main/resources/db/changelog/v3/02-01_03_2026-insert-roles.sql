-- liquibase formatted sql

-- ===================================================
-- changeset inzhevatkin.v:3-2-1
-- comment: Добавление базовых ролей
-- ===================================================
INSERT INTO bonus_system.roles (name, description) VALUES
                                                       ('BONUS_READ', 'Просмотр баланса и истории операций'),
                                                       ('BONUS_WRITE', 'Начисление, списание и возврат бонусов')
    ON CONFLICT (name) DO NOTHING;

-- ===================================================
-- changeset inzhevatkin.v:3-2-2
-- comment: Добавление административной роли
-- ===================================================
INSERT INTO bonus_system.roles (name, description) VALUES
    ('BONUS_ADMIN', 'Администрирование бонусной системы')
    ON CONFLICT (name) DO NOTHING;