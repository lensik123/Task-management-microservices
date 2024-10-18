# Task Management System

## Описание проекта

Это система управления задачами, которая позволяет создавать, отслеживать и управлять задачами в команде. В системе реализован учет времени работы над задачами и назначение ответственных за задачи пользователей. Система поддерживает различные роли пользователей:

- **ADMIN** — администратор системы с полными правами.
- **MANAGER** — менеджер, который управляет задачами и назначает ответственных.
- **USER** — обычный пользователь, который может выполнять задачи.

## Основные сущности

1. **Task (Задача):**
   - Имеет заголовок, описание, автора, исполнителя, дедлайн, статус и приоритет.
   - Связана с записями времени (TimeEntry).

2. **TimeEntry (Запись времени):**
   - Учет времени, затраченного на выполнение задачи пользователем.

3. **User (Пользователь):**
   - Пользователи могут иметь роли: `ADMIN`, `MANAGER`, или `USER`.


### Стек технологий
- **Spring Boot** — основной фреймворк для разработки приложения.
- **JPA/Hibernate** — для ORM и работы с базой данных.
- **Flyway** — для управления миграциями базы данных.
- **Kafka Spring Cloud** — для обработки сообщений через Apache Kafka.
- **Spring Security** — для аутентификации и авторизации.
- **Eureka** — для регистрации микросервисов и их взаимодействия.
- **PostgreSQL** — в качестве СУБД.


## Swagger документация
В проекте реализована централизованная Swagger документация для всех сервисов, которая доступна по следующему адресу: http://localhost:9092/swagger-ui/index.html
Swagger предоставляет возможность просматривать и взаимодействовать с API всех сервисов проекта. В верхней правой части интерфейса находится выпадающий список, где можно выбрать конкретный сервис для отображения его документации. Например:
- `auth`
- `task-management`
- `statistic`

Таким образом, вы можете легко переключаться между сервисами и их API в одном месте. 



## Инструкция по запуску.
### Конфигурация 
Перед запуском проекта убедитесь, что в `application.yml` указаны правильные параметры для подключения к базе данных (для всех сервисов):

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/task_db
    username: your_db_username
    password: your_db_password
  flyway:
    user: your_db_username
    password: your_db_password
```

### Запуск кластера Kafka и Zookeeper

Для локального развертывания кластера Apache Kafka и Zookeeper необходимо использовать Docker Compose файл `zk-single-kafka-multiple1.yml`. Этот файл содержит конфигурацию для одного экземпляра Zookeeper и нескольких брокеров Kafka.

### Шаги для запуска кластера

1. Убедитесь, что у вас установлены Docker и Docker Compose.
   
   Для проверки выполните следующие команды:

   ```bash
   docker --version
   docker-compose --version
   ```
2. Перейдите в директорию, где находится файл zk-single-kafka-multiple1.yml. 
```bash
cd path/to/taskManagerNew
```
3.Запустите кластер с помощью Docker Compose:
```bash
docker-compose -f zk-single-kafka-multiple1.yml up -d
```
