<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head><title>Редактирование клиента</title></head>
<body>
<h1>Редактирование клиента</h1>
<form:form modelAttribute="client" action="${pageContext.request.contextPath}/clients/${client.id}/edit" method="post">
    <label>ФИО: <form:input path="fullName"/></label><br>
    <label>Email: <form:input path="email"/></label><br>
    <label>Телефон: <form:input path="phone"/></label><br>
    <label>Адрес: <form:textarea path="address"/></label><br>
    <button type="submit">Сохранить</button>
</form:form>
<a href="${pageContext.request.contextPath}/clients/${client.id}">Отмена</a>
</body>
</html>