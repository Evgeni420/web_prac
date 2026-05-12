<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Выбор рейса</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
</head>
<body>
<div class="container">
    <header>
        <h1>Выберите время отправления</h1>
        <nav>
            <a href="${pageContext.request.contextPath}/">Главная</a>
            <a href="${pageContext.request.contextPath}/routes">Все рейсы</a>
        </nav>
    </header>
    <main>
        <p><strong>Маршрут:</strong> ${route.routeNumber} (${route.company.name})</p>
        <p><strong>Направление:</strong> ${route.stops[0].stopName} → ${route.stops[route.stops.size()-1].stopName}</p>
        <p><strong>Дата:</strong> ${formattedDate}</p>
        <p><strong>Цена:</strong> <fmt:formatNumber value="${price}" type="currency" currencySymbol="руб."/></p>

        <c:choose>
            <c:when test="${empty trips}">
                <div class="error">На выбранную дату нет рейсов. Попробуйте другую дату.</div>
            </c:when>
            <c:otherwise>
                <table class="trips-table">
                    <thead>
                        <tr><th>Время отправления</th><th>Действие</th></tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${trips}" var="trip">
                            <tr>
                                <td>${trip.scheduledDeparture.toLocalTime()}</td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/booking/${trip.id}?fromStopId=${fromStopId}&toStopId=${toStopId}">
                                        Забронировать
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
        <a href="javascript:history.back()">← Назад</a>
    </main>
</div>
</body>
</html>