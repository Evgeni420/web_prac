<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Добавить маршрут</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
    <link rel="stylesheet" href="//code.jquery.com/ui/1.13.2/themes/base/jquery-ui.css">
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script src="https://code.jquery.com/ui/1.13.2/jquery-ui.js"></script>
</head>
<body>
    <div class="container">
        <header>
            <h1>Добавить новый маршрут</h1>
            <nav>
                <a href="${pageContext.request.contextPath}/">Главная</a>
                <a href="${pageContext.request.contextPath}/routes">Все рейсы</a>
            </nav>
        </header>

        <main>
            <c:if test="${not empty error}">
                <div class="error">${error}</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/routes/new" method="post">
                <div class="form-group">
                    <label for="companyId">Компания:</label>
                    <select id="companyId" name="companyId" required>
                        <option value="">Выберите компанию</option>
                        <c:forEach items="${companies}" var="company">
                            <option value="${company.id}">${company.name}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label for="routeNumber">Номер маршрута:</label>
                    <input type="text" id="routeNumber" name="routeNumber" required>
                </div>

                <div class="form-group">
                    <label for="routeDescription">Описание:</label>
                    <textarea id="routeDescription" name="routeDescription" rows="3"></textarea>
                </div>

                <div class="form-group">
                    <label for="busCapacity">Вместимость автобуса:</label>
                    <input type="number" id="busCapacity" name="busCapacity" min="1" max="100" required>
                </div>

                <div class="form-group">
                    <label>Время отправления:</label>
                    <div id="departureTimes">
                        <input type="time" name="departureTimes" placeholder="HH:MM">
                    </div>
                    <button type="button" onclick="addDepartureTime()">+ Добавить время</button>
                </div>

                <div class="form-group">
                    <label>Остановки:</label>
                    <div id="stops">
                        <div class="stop-row">
                            <input type="text" name="stopName" class="stop-name" placeholder="Название остановки" required>
                            <input type="number" name="stopOffset" placeholder="Время от начала (мин)" required>
                        </div>
                    </div>
                    <button type="button" onclick="addStop()">+ Добавить остановку</button>
                </div>

                <button type="submit" class="btn btn-primary">Сохранить маршрут</button>
            </form>
        </main>
    </div>

    <script>
        let stopCount = 1;

        function addDepartureTime() {
            const container = document.getElementById('departureTimes');
            const div = document.createElement('div');
            div.className = 'time-row';
            const input = document.createElement('input');
            input.type = 'time';
            input.name = 'departureTimes';
            input.placeholder = 'HH:MM';

            const removeBtn = document.createElement('button');
            removeBtn.type = 'button';
            removeBtn.textContent = 'Удалить';
            removeBtn.onclick = function() { this.parentElement.remove(); };

            div.appendChild(input);
            div.appendChild(document.createElement('br'));
            div.appendChild(removeBtn);
            container.appendChild(div);
        }

        function addStop() {
            const container = document.getElementById('stops');
            const div = document.createElement('div');
            div.className = 'stop-row';
            div.innerHTML = `
                <input type="text" class="stop-name" name="stopName" placeholder="Название остановки" required>
                <input type="number" name="stopOffset" placeholder="Время от начала (мин)" required>
                <button type="button" onclick="this.parentElement.remove()">Удалить</button>
            `;
            container.appendChild(div);
            initAutocompleteForStop(div.querySelector('.stop-name'));
        }

        function initAutocompleteForStop(element) {
            $(element).autocomplete({
                source: function(request, response) {
                    $.getJSON("${pageContext.request.contextPath}/api/stops", { term: request.term }, response);
                },
                minLength: 1
            });
        }

        $(document).ready(function() {
            $('.stop-name').each(function() {
                initAutocompleteForStop($(this));
            });
        });
    </script>
</body>
</html>