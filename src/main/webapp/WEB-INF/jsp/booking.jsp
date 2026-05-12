<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Бронирование билета</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>Бронирование билета</h1>
            <nav>
                <a href="${pageContext.request.contextPath}/">Главная</a>
            </nav>
        </header>

        <main>
            <div class="booking-info">
                <h2>Информация о рейсе</h2>
                <p><strong>Маршрут:</strong> ${route.routeNumber} (${route.company.name})</p>
                <p><strong>Отправление:</strong> ${fromStop.stopName}</p>
                <p><strong>Прибытие:</strong> ${toStop.stopName}</p>
                <p><strong>Дата и время:</strong> ${formattedDate}</p>
                <p><strong>Цена:</strong> <fmt:formatNumber value="${price}" type="currency" currencySymbol="руб."/></p>
            </div>

            <div class="client-form">
                <h2>Данные клиента</h2>
                <form action="${pageContext.request.contextPath}/booking/confirm" method="post">
                    <input type="hidden" name="tripId" value="${trip.id}">
                    <input type="hidden" name="fromStopId" value="${fromStop.id}">
                    <input type="hidden" name="toStopId" value="${toStop.id}">
                    <input type="hidden" name="price" value="${price}">

                    <div class="form-group">
                        <label for="fullName">ФИО:</label>
                        <input type="text" id="fullName" name="clientName" required>
                    </div>

                    <div class="form-group">
                        <label for="phone">Телефон:</label>
                        <input type="tel" id="phone" name="clientPhone" required>
                    </div>

                    <div class="form-group">
                        <label for="email">Email:</label>
                        <input type="email" id="email" name="clientEmail" required>
                    </div>

                    <div class="form-group">
                        <label for="seatNumber">Номер места:</label>
                        <select id="seatNumber" name="seatNumber" required>
                            <option value="">Выберите место</option>
                            <c:forEach items="${availableSeats}" var="seat">
                                <option value="${seat}">Место ${seat}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <button type="submit" class="btn btn-primary">Подтвердить бронирование</button>
                </form>
            </div>
        </main>
    </div>
</body>
</html>