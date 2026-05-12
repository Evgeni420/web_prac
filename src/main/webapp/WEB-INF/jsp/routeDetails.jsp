<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Регистрация клиента</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>Детали маршрута</h1>
            <nav>
                <a href="${pageContext.request.contextPath}/">Главная</a>
                <a href="${pageContext.request.contextPath}/routes">Все рейсы</a>
                <a href="${pageContext.request.contextPath}/clients">Клиенты</a>
                <a href="${pageContext.request.contextPath}/clients/register">Регистрация</a>
            </nav>
        </header>

        <main>
            <a href="${pageContext.request.contextPath}/routes" class="btn-back">← Назад к списку маршрутов</a>

            <c:if test="${not empty error}">
                <div class="error-message">${error}</div>
            </c:if>

            <c:if test="${not empty route}">
                <div class="route-info">
                    <h2>Маршрут ${route.routeNumber}</h2>
                    <p><strong>Компания:</strong> ${route.company.name}</p>
                    <p><strong>Описание:</strong> ${route.routeDescription}</p>
                    <p><strong>Вместимость автобуса:</strong> ${route.busCapacity} мест</p>

                    <c:if test="${not empty route.departureTimes}">
                        <div class="departure-times">
                            <strong>Время отправления:</strong><br>
                            <c:forEach items="${route.departureTimes}" var="time">
                                <span class="departure-time">${time}</span>
                            </c:forEach>
                        </div>
                    </c:if>
                </div>

                <div class="stops-list">
                    <h3>Остановки</h3>
                    <c:forEach items="${stops}" var="stop">
                        <div class="stop-item">
                            <span class="stop-index">${stop.stopIndex + 1}</span>
                            <span class="stop-name">${stop.stopName}</span>
                            <span class="stop-offset">+${stop.offsetMinutes} мин</span>
                        </div>
                    </c:forEach>
                </div>

                <div class="booking-section">
                    <h3>Быстрое бронирование</h3>
                    <form action="${pageContext.request.contextPath}/booking/chooseTrip" method="get">
    <input type="hidden" name="routeId" value="${route.id}">
    <div class="form-group">
        <label for="fromStopId">Откуда:</label>
        <select id="fromStopId" name="fromStopId" required>
            <option value="">-- Выберите остановку --</option>
            <c:forEach items="${stops}" var="stop">
                <option value="${stop.id}">${stop.stopName}</option>
            </c:forEach>
        </select>
    </div>
    <div class="form-group">
        <label for="toStopId">Куда:</label>
        <select id="toStopId" name="toStopId" required>
            <option value="">-- Выберите остановку --</option>
            <c:forEach items="${stops}" var="stop">
                <option value="${stop.id}">${stop.stopName}</option>
            </c:forEach>
        </select>
    </div>
    <div class="form-group">
        <label for="date">Дата поездки:</label>
        <input type="date" id="date" name="date" required>
    </div>
    <button type="submit">Показать рейсы</button>
</form>
                </div>
            </c:if>
        </main>

    </div>

</body>
</html>