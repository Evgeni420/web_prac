<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Автобусные билеты - Поиск рейсов</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>Автобусные билеты</h1>
            <nav>
                <a href="${pageContext.request.contextPath}/">Главная</a>
                <a href="${pageContext.request.contextPath}/routes">Все рейсы</a>
                <a href="${pageContext.request.contextPath}/clients">Клиенты</a>
                <a href="${pageContext.request.contextPath}/clients/register">Регистрация</a>
            </nav>
        </header>

        <main>
            <div class="search-form">
                <h2>Поиск рейсов</h2>
                <form action="${pageContext.request.contextPath}/search" method="post">
    <div class="form-group">
        <label for="fromStop">Откуда:</label>
        <input type="text" id="fromStop" name="fromStop" required>
    </div>
    <div class="form-group">
        <label for="toStop">Куда:</label>
        <input type="text" id="toStop" name="toStop" required>
    </div>
    <div class="form-group">
        <label for="date">Дата:</label>
        <input type="date" id="date" name="date" required>
    </div>
    <button type="submit">Найти</button>
</form>
            </div>

        </main>

    </div>

    <script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>