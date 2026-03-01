# app-posting-client-bonuses

## Описание

Для старта приложения необходимо поднять тестовую базу данных через
запуск работы модуля 'docker-postgres'

### Запуск PostgreSQL

```powershell
# Перейти в папку модуля
cd docker-postgres

# Запустить контейнер в фоне
docker-compose up -d
```

При поднятии контейнера, выполняется скрипт инициализации схеммы базы данных
и необходимых для работы таблиц

После поднятия контейнера, можно запускать основное приложение.

### Удобный способ запуска приложения

#### В пакете app-posting-client-bonuses-app, найти Application.java класс и запустить

### Немного менее удобный способ запуска приложения)

```powershell
# Вернуться в корневую папку проекта
cd ..

# Перейти в папку spring-приложения
cd app-posting-client-bonuses-app

# Очистить и собрать проект
mvn clean package

# Запустить
mvn spring-boot:run
```

# В приложении реализовано:

### Отдельные кастомные метрики на каждый контроллер

### Сквозная межсервисная трассировка запросов с использованием micrometer

### Надёжная транзакционнось + оптимизация

### Защита от DOS-атак и скачков трафика посредствам @RateLimiter

### Кэширование баланса пользователя с использованием Caffeine

### Комбинация оптимистичных и пессимистичных блокировок на операции изменения данных

### Пагинация для операций получения истории транзакций

### Документация Swagger по адрессу 'http://localhost:8080/api/swagger-ui/index.html#/'

### Liquibase

### Авторизация и аутентификация пользователей с 3-мя ролями

# Работа с авторизацией пользователей:

1) Получить jwt токен для авторизации, введя данные одного из пользователей, которые есть в бд
   отправив POST /v1/auth/login с телом:
   {
   "username": "имя пользователя",
   "password": "пароль"
   }
```text
# Примеры готовых пользователей:

- роль BONUS_ADMIN - CRUD-операции с пользователями
{
   "username": "valeriy",
   "password": "password"
}

- роль BONUS_WRITE - операции чтения баланса
{
   "username": "petr.writer",
   "password": "2"
}

- роль BONUS_READ - операции изменения баланса
{
   "username": "anna.reader",
   "password": "1"
}

- роль BONUS_READ + BONUS_WRITE - любые операции с бонусами
{
   "username": "elena.both",
   "password": "3"
}
```

2) После получения тела ответа в формате:

```text
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlbGVuYS5ib3RoIiwiaWF0IjoxNzcyMzk0ODE3LCJleHAiOjE3NzI0ODEyMTd9.NBg0FWF1GSZsqkqY6AksQ37Eji93lvh_eqo5cIJBKGc",
  "username": "elena.both",
  "roles": [
    "BONUS_READ",
    "BONUS_WRITE"
  ]
}
```
вставляем полученый токен без кавычек в поле для токена(swagger) или 
```text
-H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
```

После этих операций будет обеспечен доступ к эндпойнтам, согласно нашей роли

## P.s.
В связи с ограниченным количеством времени, не успел проработать архитектуру модуля security. Функционал реализован
полностью, задача выполнена, однако такое решение плохо способствует расширяемости сервиса. Знаю как сделать лучше)