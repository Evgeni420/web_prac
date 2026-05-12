<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Результаты поиска</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>Результаты поиска</h1>
            <nav>
                <a href="${pageContext.request.contextPath}/">Главная</a>
                <a href="${pageContext.request.contextPath}/routes">Все рейсы</a>
            </nav>
        </header>

        <main>
            <div class="search-summary">
                <h3>Поиск: ${fromStop} → ${toStop}</h3>
                <p>Дата: ${date}</p>
            </div>

            <c:choose>
                <c:when test="${empty routes}">
                    <p>По вашему запросу ничего не найдено. Попробуйте изменить параметры поиска.</p>
                </c:when>
                <c:otherwise>
                    <table class="routes-table">
                        <thead>
                            <tr>
                                <th>Маршрут</th>
                                <th>Компания</th>
                                <th>Направление</th>
                                <th>Действие</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${routes}" var="route">
                                <tr>
                                    <td>${route.routeNumber}</td>
                                    <td>${route.company.name}</td>
                                    <c:set var="stops" value="${route.stops}" />
                                    <td>
                                        <c:if test="${not empty stops}">
                                            ${stops[0].stopName} → ${stops[stops.size()-1].stopName}
                                        </c:if>
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/routes/${route.id}" class="btn-details">Выбрать рейс</a>
                                    </td>
                                </table>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </main>
    </div>
</body>
</html>