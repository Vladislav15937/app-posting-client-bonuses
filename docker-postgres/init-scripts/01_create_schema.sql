-- Создаем схему
CREATE SCHEMA IF NOT EXISTS bonus_system;

-- =================================================================
-- 1. Таблица карт
-- =================================================================
CREATE TABLE IF NOT EXISTS bonus_system.card
(
    id          BIGSERIAL PRIMARY KEY,
    card_number VARCHAR(20) UNIQUE NOT NULL,
    balance     BIGINT             NOT NULL DEFAULT 0,
    status      VARCHAR(20)        NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'BLOCKED', 'CLOSED')),
    client_id   VARCHAR(50),
    created_at  TIMESTAMP                   DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP                   DEFAULT CURRENT_TIMESTAMP,
    created_by  VARCHAR(50),
    updated_by  VARCHAR(50),
    version     BIGINT                      DEFAULT 0
);

COMMENT ON TABLE bonus_system.card IS 'Бонусные карты клиентов';
COMMENT ON COLUMN bonus_system.card.id IS 'Уникальный идентификатор карты (первичный ключ)';
COMMENT ON COLUMN bonus_system.card.card_number IS 'Номер карты (уникальный)';
COMMENT ON COLUMN bonus_system.card.balance IS 'Баланс в копейках';
COMMENT ON COLUMN bonus_system.card.status IS 'Статус карты: ACTIVE, BLOCKED, CLOSED';
COMMENT ON COLUMN bonus_system.card.client_id IS 'Идентификатор клиента-владельца карты (внешний ключ к таблице clients)';
COMMENT ON COLUMN bonus_system.card.created_at IS 'Дата и время создания записи';
COMMENT ON COLUMN bonus_system.card.updated_at IS 'Дата и время последнего обновления записи';
COMMENT ON COLUMN bonus_system.card.created_by IS 'Идентификатор пользователя, создавшего запись';
COMMENT ON COLUMN bonus_system.card.updated_by IS 'Идентификатор пользователя, последним обновившего запись';
COMMENT ON COLUMN bonus_system.card.version IS 'Версия для оптимистичной блокировки';

-- =================================================================
-- 2. Таблица транзакций
-- =================================================================
CREATE TABLE IF NOT EXISTS bonus_system.transaction
(
    id                    BIGSERIAL PRIMARY KEY,
    card_id               BIGINT      NOT NULL,
    transaction_uuid      UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
    amount                BIGINT      NOT NULL,
    type                  VARCHAR(20) NOT NULL CHECK (type IN ('ACCRUAL', 'WRITE_OFF', 'REFUND', 'CORRECTION')),
    balance_before        BIGINT      NOT NULL,
    balance_after         BIGINT      NOT NULL,
    description           VARCHAR(500),
    external_id           VARCHAR(100),
    external_system       VARCHAR(50),
    status                VARCHAR(20)          DEFAULT 'COMPLETED' CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED', 'CANCELLED')),
    created_at            TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
    created_by            VARCHAR(50),
    parent_transaction_id BIGINT,

    CONSTRAINT fk_transaction_card FOREIGN KEY (card_id) REFERENCES bonus_system.card (id) ON DELETE CASCADE,
    CONSTRAINT fk_transaction_parent FOREIGN KEY (parent_transaction_id) REFERENCES bonus_system.transaction (id)
);

COMMENT ON TABLE bonus_system.transaction IS 'История операций по картам';
COMMENT ON COLUMN bonus_system.transaction.id IS 'Уникальный идентификатор транзакции (первичный ключ)';
COMMENT ON COLUMN bonus_system.transaction.card_id IS 'Идентификатор карты (внешний ключ к таблице card)';
COMMENT ON COLUMN bonus_system.transaction.transaction_uuid IS 'UUID для идемпотентности (уникальный)';
COMMENT ON COLUMN bonus_system.transaction.amount IS 'Сумма в копейках (+ начисление, - списание)';
COMMENT ON COLUMN bonus_system.transaction.type IS 'Тип операции: ACCRUAL, WRITE_OFF, REFUND, CORRECTION';
COMMENT ON COLUMN bonus_system.transaction.balance_before IS 'Баланс карты до операции';
COMMENT ON COLUMN bonus_system.transaction.balance_after IS 'Баланс карты после операции';
COMMENT ON COLUMN bonus_system.transaction.description IS 'Описание операции';
COMMENT ON COLUMN bonus_system.transaction.external_id IS 'Идентификатор во внешней системе';
COMMENT ON COLUMN bonus_system.transaction.external_system IS 'Название внешней системы';
COMMENT ON COLUMN bonus_system.transaction.status IS 'Статус транзакции: PENDING, COMPLETED, FAILED, CANCELLED';
COMMENT ON COLUMN bonus_system.transaction.created_at IS 'Дата и время создания транзакции';
COMMENT ON COLUMN bonus_system.transaction.created_by IS 'Идентификатор пользователя, создавшего транзакцию';
COMMENT ON COLUMN bonus_system.transaction.parent_transaction_id IS 'Идентификатор родительской транзакции (для возвратов)';