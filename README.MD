# Задание 1

Создайте новый репозиторий, пусть init скоммит содержит только readme и gitignore

CRUD приложение + логи через аспекты

1. Создать простой REST сервис для управления задачами:

Task(id, title, description,userId)

1. POST /tasks — создание новой задачи.

2. GET /tasks/{id} — получение задачи по ID.

3. PUT /tasks/{id} — обновление задачи.

4. DELETE /tasks/{id} — удаление задачи.

5. GET /tasks — получение списка всех задач.

3. Релизуйте класс аспект, со следующими advice:

1. Before

2. AfterThrowing

3. AfterReturning

4. Around (замер выполнения)

В приложении должна быть реализована логика на каждый advice - свой метод, можно сделать больше, использовать несколько advice на отдельные методы, но меньше нельзя.

Присылайте ссылку на PR в личном кабинете на LMS для проверки.

Задание 2 Дедлайн 12.05.2025
1. Использовать docker-compose для установки Kafka в Docker (пример демонстрируется на уроке)

1.1 Установить необходимые инструменты для работы с Kafka. Big Data Tools, Offset Explorer и др. на свое усмотрение и возможности, результатом должна быть возможность "заглянуть" в кафку

2. Создать тестовый topic, установленными средствами

3. Отправить в топик тестовые сообщения

4. Убедиться в их наличии

5. Сконфигурировать Kafka, Producer, Consumer в вашем сервисе рабты с Task.

6. Продюсер пишет в топик id и новый статус task у которых он изменился, при соответствующем входящем запросе (обновления task).

7. Консьюмер слушает этот топик, читает оттуда сообщения, и отправляет в NotificationService, этот класс должен отправлять уведомление на почту о смене статуса, полученном от консьюмера

8. Использовать spring-boot-starter-mail, для отправки email в NotificationService