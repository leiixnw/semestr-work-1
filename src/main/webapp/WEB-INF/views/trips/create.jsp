<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="my" tagdir="/WEB-INF/tags" %>

<my:layout title="Создать поездку">
    <div class="nav">
        <a href="${pageContext.request.contextPath}/main">← Назад</a>
    </div>

    <h1>Создать поездку</h1>

    <div class="card">
        <form action="${pageContext.request.contextPath}/trip/create" method="post" onsubmit="return validateForm(this)">
            <div class="form-group">
                <label>Название поездки:</label>
                <input type="text" name="title" required>
            </div>

            <div class="form-group">
                <label>Описание:</label>
                <textarea name="description" required></textarea>
            </div>

            <div class="form-group">
                <label>Направление:</label>
                <input type="text" name="destination" required>
            </div>

            <div class="form-group">
                <label>Дата начала:</label>
                <input type="date" name="startDate" required>
            </div>

            <div class="form-group">
                <label>Дата окончания:</label>
                <input type="date" name="endDate" required>
            </div>

            <div class="form-group">
                <label>Максимум участников:</label>
                <input type="number" name="maxParticipants" min="1" required>
            </div>

            <button type="submit" class="btn">Создать поездку</button>
        </form>
    </div>
</my:layout>