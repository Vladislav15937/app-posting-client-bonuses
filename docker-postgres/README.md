# PostgreSQL Database Module for Bonus System

## Описание

Модуль для запуска PostgreSQL в Docker контейнере. Автоматически создаёт схему `bonus_system` с таблицами для бонусного
сервиса. **Таблицы создаются пустыми**, без тестовых данных.

## Управление контейнером

### Запуск PostgreSQL

```powershell
# Перейти в папку модуля
cd docker-postgres

# Запустить контейнер в фоне
docker-compose up -d

# Запустить и видеть логи в реальном времени
docker-compose up

# Остановить контейнер (сохранить данные)
docker-compose stop

# Остановить и удалить контейнер (данные сохранятся в volume)
docker-compose down

# Остановить, удалить контейнер и volume (удалит ВСЕ данные)
docker-compose down -v

# Перезапустить контейнер
docker-compose restart

# Полная перезагрузка (пересоздать контейнер)
docker-compose down
docker-compose up -d