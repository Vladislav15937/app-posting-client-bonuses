-- liquibase formatted sql

-- ===================================================
-- changeset inzhevatkin.v:2-2-1
-- comment: Вставка новой тестовой карты
-- precondition: tableExists card
-- ===================================================
INSERT INTO bonus_system.card (card_number, balance, status, client_id, created_by)
VALUES ('1111111111', 30000, 'ACTIVE', 'client_6', 'SYSTEM') ON CONFLICT (card_number) DO NOTHING;

-- ===================================================
-- changeset inzhevatkin.v:2-2-2
-- comment: Добавление транзакций для новой карты (приветственный бонус)
-- ===================================================
INSERT INTO bonus_system.transaction (card_id, transaction_uuid, amount, type, balance_before, balance_after,
                                      description, created_by)
SELECT c.id,
       gen_random_uuid(),
       30000,
       'ACCRUAL',
       0,
       30000,
       'Приветственный бонус для новой карты',
       'SYSTEM'
FROM bonus_system.card c
WHERE c.card_number = '1111111111'
  AND NOT EXISTS (SELECT 1
                  FROM bonus_system.transaction t
                  WHERE t.card_id = c.id
                    AND t.description = 'Приветственный бонус для новой карты');

-- ===================================================
-- changeset inzhevatkin.v:2-2-3
-- comment: Добавление транзакции списания
-- ===================================================
INSERT INTO bonus_system.transaction (card_id, transaction_uuid, amount, type, balance_before, balance_after,
                                      description, created_by)
SELECT c.id,
       gen_random_uuid(),
       -500,
       'WRITE_OFF',
       30000,
       29500,
       'Пробная покупка',
       'SYSTEM'
FROM bonus_system.card c
WHERE c.card_number = '1111111111'
  AND NOT EXISTS (SELECT 1
                  FROM bonus_system.transaction t
                  WHERE t.card_id = c.id
                    AND t.description = 'Пробная покупка');

-- ===================================================
-- changeset inzhevatkin.v:2-2-4
-- comment: Добавление транзакции возврата
-- ===================================================
INSERT INTO bonus_system.transaction (card_id, transaction_uuid, amount, type, balance_before, balance_after,
                                      description, created_by)
SELECT c.id,
       gen_random_uuid(),
       1000,
       'REFUND',
       29500,
       30500,
       'Возврат пробной покупки',
       'SYSTEM'
FROM bonus_system.card c
WHERE c.card_number = '1111111111'
  AND NOT EXISTS (SELECT 1
                  FROM bonus_system.transaction t
                  WHERE t.card_id = c.id
                    AND t.description = 'Возврат пробной покупки');

-- ===================================================
-- changeset inzhevatkin.v:2-2-5
-- comment: Обновление баланса карты
-- ===================================================
UPDATE bonus_system.card
SET balance = 30500
WHERE card_number = '1111111111';