<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head><title>Редактирование компании</title></head>
<body>
<h1>Редактирование компании</h1>
<form:form modelAttribute="company" action="${pageContext.request.contextPath}/companies/${company.id}/edit" method="post">
    <label>Название: <form:input path="name"/></label><br>
    <button type="submit">Сохранить</button>
</form:form>
<a href="${pageContext.request.contextPath}/companies/${company.id}">Отмена</a>
</body>
</html>