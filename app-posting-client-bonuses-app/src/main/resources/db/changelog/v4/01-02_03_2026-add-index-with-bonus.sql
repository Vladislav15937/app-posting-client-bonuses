-- liquibase formatted sql

-- ===================================================
-- changeset inzhevatkin.v:4-1-1 runInTransaction:false
-- comment: Создание индексов для оптимизации производительности
-- ===================================================

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_card_number
    ON bonus_system.card (card_number);

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_transaction_card_id
    ON bonus_system.transaction (card_id);

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_transaction_card_created
    ON bonus_system.transaction (card_id, created_at DESC);

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_transaction_parent
    ON bonus_system.transaction (parent_transaction_id);

CREATE UNIQUE INDEX CONCURRENTLY IF NOT EXISTS idx_transaction_uuid
    ON bonus_system.transaction (transaction_uuid);

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_transaction_created
    ON bonus_system.transaction (created_at);