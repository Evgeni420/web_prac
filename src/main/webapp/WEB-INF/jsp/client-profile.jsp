<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Профиль клиента</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>Профиль клиента</h1>
            <nav>
                <a href="${pageContext.request.contextPath}/">Главная</a>
                <a href="${pageContext.request.contextPath}/routes">Все рейсы</a>
                <a href="${pageContext.request.contextPath}/clients">Клиенты</a>
            </nav>
        </header>
        
        <main>
            <c:if test="${not empty success}">
                <div class="success-message" style="color: green; padding: 10px; margin: 10px 0; background: #eeffee; border-radius: 5px;">
                    ${success}
                </div>
            </c:if>
            
            <c:if test="${not empty error}">
                <div class="error-message" style="color: red; padding: 10px; margin: 10px 0; background: #ffeeee; border-radius: 5px;">
                    ${error}
                </div>
            </c:if>
            
            <c:if test="${not empty client}">
                <div class="client-info">
                    <h2>Информация о клиенте</h2>
                    <form action="${pageContext.request.contextPath}/clients/${client.id}/edit" method="post">
                        
                        <div class="form-group">
                            <label for="fullName">ФИО:</label>
                            <input type="text" id="fullName" name="fullName" value="${client.fullName}" required>
                        </div>
                        
                        <div class="form-group">
                            <label for="email">Email:</label>
                            <input type="email" id="email" name="email" value="${client.email}" required>
                        </div>
                        
                        <div class="form-group">
                            <label for="phone">Телефон:</label>
                            <input type="tel" id="phone" name="phone" value="${client.phone}">
                        </div>
                        
                        <div class="form-group">
                            <label for="address">Адрес:</label>
                            <textarea id="address" name="address" rows="3">${client.address}</textarea>
                        </div>
                        
                        <button type="submit" class="btn btn-primary">Сохранить изменения</button>
                    </form>
                </div>
                
                <div class="booking-history">
                    <h2>История бронирований</h2>
                    <c:choose>
                        <c:when test="${empty bookings}">
                            <p>У клиента нет бронирований.</p>
                        </c:when>
                        <c:otherwise>
                            <table class="bookings-table">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Маршрут</th>
                                        <th>Откуда</th>
                                        <th>Куда</th>
                                        <th>Дата поездки</th>
                                        <th>Место</th>
                                        <th>Цена</th>
                                        <th>Статус</th>
                                        <th>Дата бронирования</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${bookings}" var="booking">
                                        <tr>
                                            <td>${booking.id}</td>
                                            <td>${booking.trip.route.routeNumber}</td>
                                            <td>${booking.fromStop.stopName}</td>
                                            <td>${booking.toStop.stopName}</td>
                                            <td>${booking.trip.scheduledDeparture}</td>
                                            <td>${booking.seatNumber}</td>
                                            <td>${booking.price} руб.</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${booking.status == 'booked'}">
                                                        <span style="color: orange;">Забронирован</span>
                                                    </c:when>
                                                    <c:when test="${booking.status == 'paid'}">
                                                        <span style="color: green;">Оплачен</span>
                                                    </c:when>
                                                    <c:when test="${booking.status == 'cancelled'}">
                                                        <span style="color: red;">Отменён</span>
                                                    </c:when>
                                                </c:choose>
                                            </td>
                                            <td>${booking.createdAt}</td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:if>
        </main>

    </div>
</body>
</html>