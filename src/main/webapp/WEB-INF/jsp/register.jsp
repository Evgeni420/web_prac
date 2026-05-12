<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
            <h1>Регистрация нового клиента</h1>
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

            <form action="${pageContext.request.contextPath}/clients/register" method="post">
                <div class="form-group">
                    <label for="fullName">ФИО *:</label>
                    <input type="text" id="fullName" name="fullName" required
                           value="${param.fullName}">
                </div>

                <div class="form-group">
                    <label for="email">Email *:</label>
                    <input type="email" id="email" name="email" required
                           value="${param.email}">
                </div>

                <div class="form-group">
                    <label for="phone">Телефон:</label>
                    <input type="tel" id="phone" name="phone"
                           value="${param.phone}" placeholder="+7 (999) 123-45-67">
                </div>

                <div class="form-group">
                    <label for="address">Адрес:</label>
                    <textarea id="address" name="address" rows="3">${param.address}</textarea>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn btn-primary">Зарегистрироваться</button>
                    <a href="${pageContext.request.contextPath}/" class="btn">Отмена</a>
                </div>
            </form>
        </main>

    </div>
</body>
</html>