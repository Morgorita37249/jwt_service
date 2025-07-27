# Spring Boot JWT Authentication

Этот проект реализует систему аутентификации и авторизации с использованием:

- Spring Boot 3

- Spring Security
 
- JWT токены (Access + Refresh)
  
- H2 база данных в памяти

- REST API для регистрации, входа, обновления и выхода из системы

# Примеры запросов

# Регистрация

POST /auth/register

Content-Type: application/json

{

  "username": "testuser",
  
  "password": "test123",
  
  "email": "test@example.com"
  
}

# Вход

POST /auth/login


{

  "username": "testuser",
  
  "password": "test123"
  
}

# Ответ:

{

  "token": "<ACCESS_TOKEN>",
  
  "refreshToken": "<REFRESH_TOKEN>"
  
}

# Доступ к защищенному API

GET /api/hello

Authorization: Bearer <ACCESS_TOKEN>

# Обновление токена

POST /auth/refresh

Authorization: Bearer <REFRESH_TOKEN>

# Ответ:

{
  "token": "<NEW_ACCESS_TOKEN>",
  
  "refreshToken": "<NEW_REFRESH_TOKEN>"
}

# Logout (отзыв токена)

POST /auth/logout

Authorization: Bearer <ACCESS_TOKEN>

# Роли

ADMIN

PREMIUM_USER

GUEST (по умолчанию)

# Примечания

Access токен живёт 30 минут.

Refresh токен — 7 дней.

Все токены хранятся в H2 в таблице token.
