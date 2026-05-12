<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head><title>Бронирование успешно</title></head>
<body>
<h1>Бронирование успешно!</h1>
<p>Ваш билет забронирован. Номер бронирования: ${booking.id}</p>
<p><strong>Маршрут:</strong> ${routeNumber} (${companyName})</p>
<p><strong>Отправление:</strong> ${formattedDeparture}</p>
<p><strong>Место:</strong> ${seatNumber}</p>
<p><strong>Цена:</strong> ${booking.price} руб.</p>
<a href="${pageContext.request.contextPath}/">На главную</a>
</body>
</html>