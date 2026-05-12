<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head><title>Компании-перевозчики</title></head>
<body>
<h1>Список компаний</h1>
<table border="1">
    <tr><th>ID</th><th>Название</th><th>Действия</th></tr>
    <c:forEach items="${companies}" var="c">
        <tr>
            <td>${c.id}</td><td>${c.name}</td>
            <td><a href="${pageContext.request.contextPath}/companies/${c.id}">Подробнее</a> |
                <a href="${pageContext.request.contextPath}/companies/${c.id}/edit">Редактировать</a>
            </td>
        </tr>
    </c:forEach>
</table>
<a href="${pageContext.request.contextPath}/">Главная</a>
</body>
</html>