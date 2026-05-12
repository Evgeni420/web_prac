<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Список маршрутов</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>Список маршрутов</h1>
            <nav>
                <a href="${pageContext.request.contextPath}/">Главная</a>
                <a href="${pageContext.request.contextPath}/routes">Все рейсы</a>
                <a href="${pageContext.request.contextPath}/routes/new" class="btn-add">Добавить маршрут</a>
            </nav>
        </header>

        <main>
            <div class="sort-controls">
                <span>Сортировать по:</span>
                <a href="${pageContext.request.contextPath}/routes?sort=price">Цене</a>
                <a href="${pageContext.request.contextPath}/routes?sort=stops">Количеству остановок</a>
            </div>

            <table class="routes-table">
                <thead>
                    <tr>
                        <th>Номер рейса</th>
                        <th>Компания</th>
                        <th>Маршрут</th>
                        <th>Остановки</th>
                        <th>Время отправления</th>
                        <th>Действия</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${routes}" var="route">
                        <tr>
                            <td>${route.routeNumber}</td>
                            <td>${route.company.name}</td>
                            <td>
                                ${route.stops[0].stopName} → 
                                ${route.stops[route.stops.size()-1].stopName}
                            </td>
                            <td>${route.stops.size()}</td>
                            <td>
                                <c:forEach items="${route.departureTimes}" var="time">
                                    ${time}<br>
                                </c:forEach>
                            </td>
                            <td>
                                <a href="${pageContext.request.contextPath}/routes/${route.id}" class="btn-details">Подробнее</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </main>
    </div>
</body>
</html>