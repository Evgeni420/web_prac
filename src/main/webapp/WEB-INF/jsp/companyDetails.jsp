<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head><title>Компания ${company.name}</title></head>
<body>
<h1>${company.name}</h1>
<p>Дата создания: ${company.createdAt}</p>
<h2>Маршруты компании</h2>
<ul>
    <c:forEach items="${company.routes}" var="r">
        <li><a href="${pageContext.request.contextPath}/routes/${r.id}">${r.routeNumber} – ${r.routeDescription}</a></li>
    </c:forEach>
</ul>
<a href="${pageContext.request.contextPath}/companies/${company.id}/edit">Редактировать</a>
<a href="${pageContext.request.contextPath}/companies">Назад</a>
</body>
</html>