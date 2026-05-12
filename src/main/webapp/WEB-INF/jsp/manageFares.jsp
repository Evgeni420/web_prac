<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head><title>Тарифы маршрута ${route.routeNumber}</title></head>
<body>
<h1>Тарифы маршрута ${route.routeNumber}</h1>
<table border="1">
    <tr><th>Откуда</th><th>Куда</th><th>Цена (руб)</th><th>Время (мин)</th><th></th></tr>
    <c:forEach items="${fares}" var="f">
        <tr>
            <td>${f.fromStop.stopName}</td><td>${f.toStop.stopName}</td>
            <td>${f.price}</td><td>${f.travelTimeMinutes}</td>
            <td>
                <form action="${pageContext.request.contextPath}/fares/${f.id}/delete" method="post">
                    <button type="submit">Удалить</button>
                </form>
            </td>
        </tr>
    </c:forEach>
</table>
<h2>Добавить тариф</h2>
<form action="${pageContext.request.contextPath}/fares/route/${route.id}/add" method="post">
    <select name="fromStopId">
        <c:forEach items="${stops}" var="s"><option value="${s.id}">${s.stopName}</option></c:forEach>
    </select>
    →
    <select name="toStopId">
        <c:forEach items="${stops}" var="s"><option value="${s.id}">${s.stopName}</option></c:forEach>
    </select>
    Цена: <input type="number" step="0.01" name="price" required>
    Время (мин): <input type="number" name="travelTimeMinutes" required>
    <button type="submit">Добавить</button>
</form>
<a href="${pageContext.request.contextPath}/routes/${route.id}">Вернуться к маршруту</a>
</body>
</html>