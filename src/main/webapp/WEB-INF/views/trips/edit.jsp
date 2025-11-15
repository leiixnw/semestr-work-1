<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="my" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<my:layout title="Редактировать поездку">
    <div class="nav">
        <a href="${pageContext.request.contextPath}/trip?id=${trip.id}">← Назад к поездке</a>
    </div>

    <h1>Редактировать поездку</h1>

    <c:if test="${not empty error}">
        <div class="alert alert-error">${error}</div>
    </c:if>

    <div class="card">
        <form action="${pageContext.request.contextPath}/trip/edit" method="post">
            <input type="hidden" name="id" value="${trip.id}">

            <div class="form-group">
                <label>Название поездки:</label>
                <input type="text" name="title" value="${trip.title}" required>
            </div>

            <div class="form-group">
                <label>Описание:</label>
                <textarea name="description" required>${trip.description}</textarea>
            </div>

            <div class="form-group">
                <label>Направление:</label>
                <input type="text" name="destination" value="${trip.destination}" required>
            </div>

            <div class="form-group">
                <label>Дата начала:</label>
                <input type="date" name="startDate" value="${trip.startDate}" required>
            </div>

            <div class="form-group">
                <label>Дата окончания:</label>
                <input type="date" name="endDate" value="${trip.endDate}" required>
            </div>

            <div class="form-group">
                <label>Максимум участников:</label>
                <input type="number" name="maxParticipants" value="${trip.maxParticipants}" min="1" required>
            </div>

            <div class="form-group">
                <label>Статус:</label>
                <select name="status">
                    <option value="ACTIVE" ${trip.status == 'ACTIVE' ? 'selected' : ''}>Актуальна</option>
                    <option value="COMPLETED" ${trip.status == 'COMPLETED' ? 'selected' : ''}>Завершена</option>
                    <option value="CANCELLED" ${trip.status == 'CANCELLED' ? 'selected' : ''}>Отменена</option>
                </select>
            </div>

            <button type="submit" class="btn">Сохранить изменения</button>
            <a href="${pageContext.request.contextPath}/trip?id=${trip.id}" class="btn btn-danger">Отмена</a>
        </form>
    </div>
</my:layout>