-- liquibase formatted sql

-- ===================================================
-- changeset inzhevatkin.v:2-1-1
-- comment: Вставка тестовых карт
-- ===================================================
INSERT INTO bonus_system.card (card_number, balance, status, client_id, created_by)
VALUES ('1234567890', 10000, 'ACTIVE', 'client_1', 'SYSTEM'),
       ('0987654321', 5000, 'ACTIVE', 'client_2', 'SYSTEM'),
       ('5555555555', 25000, 'ACTIVE', 'client_3', 'SYSTEM'),
       ('7777777777', 0, 'ACTIVE', 'client_4', 'SYSTEM'),
       ('9999999999', 15000, 'ACTIVE', 'client_5', 'SYSTEM')
ON CONFLICT (card_number) DO NOTHING;

-- ===================================================
-- changeset inzhevatkin.v:2-1-2
-- comment: Вставка транзакций
-- ===================================================
INSERT INTO bonus_system.transaction (card_id, transaction_uuid, amount, type, balance_before, balance_after,
                                      description, created_by)
VALUES ((SELECT id FROM bonus_system.card WHERE card_number = '1234567890'), gen_random_uuid(), 10000, 'ACCRUAL', 0,
        10000, 'Приветственный бонус', 'SYSTEM'),
       ((SELECT id FROM bonus_system.card WHERE card_number = '0987654321'), gen_random_uuid(), 5000, 'ACCRUAL', 0,
        5000, 'Приветственный бонус', 'SYSTEM'),
       ((SELECT id FROM bonus_system.card WHERE card_number = '5555555555'), gen_random_uuid(), 25000, 'ACCRUAL', 0,
        25000, 'Приветственный бонус', 'SYSTEM'),
       ((SELECT id FROM bonus_system.card WHERE card_number = '1234567890'), gen_random_uuid(), -500, 'WRITE_OFF',
        10000, 9500, 'Покупка кофе', 'SYSTEM'),
       ((SELECT id FROM bonus_system.card WHERE card_number = '1234567890'), gen_random_uuid(), -200, 'WRITE_OFF', 9500,
        9300, 'Покупка воды', 'SYSTEM'),
       ((SELECT id FROM bonus_system.card WHERE card_number = '5555555555'), gen_random_uuid(), -1000, 'WRITE_OFF',
        25000, 24000, 'Покупка обеда', 'SYSTEM');

-- ===================================================
-- changeset inzhevatkin.v:2-1-3
-- comment: Обновление балансов
-- ===================================================
UPDATE bonus_system.card
SET balance = 9300
WHERE card_number = '1234567890';
UPDATE bonus_system.card
SET balance = 5000
WHERE card_number = '0987654321';
UPDATE bonus_system.card
SET balance = 24000
WHERE card_number = '5555555555';