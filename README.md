# Raii panamki

## Быстрый старт

### Предварительные требования
- PHP 8.2+
- Composer 2.0+
- СУБД (PostgreSQL)

1. Установка зависимостей:
```
composer install
composer update
```
2. Настройка окружения:
```
cp .env.example .env
php artisan key:generate
```
3. Настройка БД в .env:
```
DB_CONNECTION=mysql
DB_HOST=127.0.0.1
DB_PORT=3306
DB_DATABASE=rail_panamkii
DB_USERNAME=логин
DB_PASSWORD=пароль
```
4. Запуск миграций:
```
php artisan migrate
```
5. Запуск проекта
```
php artisan serve
```
