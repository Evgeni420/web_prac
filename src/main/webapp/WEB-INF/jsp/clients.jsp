<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Список клиентов</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>Список клиентов</h1>
            <nav>
                <a href="${pageContext.request.contextPath}/">Главная</a>
                <a href="${pageContext.request.contextPath}/routes">Все рейсы</a>
                <a href="${pageContext.request.contextPath}/clients/register">Регистрация</a>
            </nav>
        </header>

        <main>
            <div class="search-form">
                <h3>Поиск клиентов</h3>
                <form action="${pageContext.request.contextPath}/clients" method="get">
                    <div class="form-group">
                        <input type="text" name="search" placeholder="Поиск по имени, email или телефону"
                               value="${param.search}">
                        <button type="submit" class="btn">Найти</button>
                        <a href="${pageContext.request.contextPath}/clients" class="btn">Сбросить</a>
                    </div>
                </form>
            </div>

            <c:if test="${not empty error}">
                <div class="error-message" style="color: red; padding: 10px; margin: 10px 0; background: #ffeeee; border-radius: 5px;">
                    ${error}
                </div>
            </c:if>

            <c:choose>
                <c:when test="${empty clients}">
                    <p>Клиенты не найдены.</p>
                </c:when>
                <c:otherwise>
                    <table class="clients-table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>ФИО</th>
                                <th>Email</th>
                                <th>Телефон</th>
                                <th>Адрес</th>
                                <th>Дата регистрации</th>
                                <th>Действия</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${clients}" var="client">
                                <tr>
                                    <td>${client.id}</td>
                                    <td>${client.fullName}</td>
                                    <td>${client.email}</td>
                                    <td>${client.phone}</td>
                                    <td>${client.address}</td>
                                    <td>${client.createdAt.toString().substring(0, 19).replace('T', ' ')}</td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/clients/${client.id}" class="btn-details">Просмотр</a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </main>

    </div>
</body>
</html>