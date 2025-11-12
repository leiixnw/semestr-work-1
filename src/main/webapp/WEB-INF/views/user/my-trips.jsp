<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="my" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<my:layout title="Мои поездки">
    <my:nav/>

    <h1>Мои поездки</h1>

    <c:if test="${not empty param.success}">
        <div class="alert alert-success">${param.success}</div>
    </c:if>

    <c:if test="${not empty param.error}">
        <div class="alert alert-error">${param.error}</div>
    </c:if>

    <div class="action-bar">
        <a href="${pageContext.request.contextPath}/trip/create" class="btn">Создать новую поездку</a>
    </div>

    <div class="trip-list">
        <c:forEach items="${myTrips}" var="trip">
            <div class="card">
                <h3>${trip.title}</h3>
                <div class="trip-meta">
                    <p><strong>📍 Направление:</strong> ${trip.destination}</p>
                    <p><strong>📅 Даты:</strong>
                            ${trip.formattedStartDate} - ${trip.formattedEndDate}
                    </p>
                    <p><strong>👥 Участники:</strong> ${trip.currentParticipants}/${trip.maxParticipants}</p>
                    <p><strong>📊 Статус:</strong> ${trip.status}</p>
                </div>

                <div class="action-buttons">
                    <a href="${pageContext.request.contextPath}/trip?id=${trip.id}"
                       class="btn">Посмотреть</a>
                    <a href="${pageContext.request.contextPath}/trip/edit?id=${trip.id}"
                       class="btn">Редактировать</a>
                    <form action="${pageContext.request.contextPath}/trip/delete"
                          method="post" style="display:inline;">
                        <input type="hidden" name="id" value="${trip.id}">
                        <button type="submit" class="btn btn-danger"
                                onclick="return confirmAction('Удалить эту поездку?')">
                            Удалить
                        </button>
                    </form>
                </div>
            </div>
        </c:forEach>

        <c:if test="${empty myTrips}">
            <div class="card">
                <p>Вы еще не создали ни одной поездки.</p>
                <a href="${pageContext.request.contextPath}/trip/create" class="btn">Создать первую поездку</a>
            </div>
        </c:if>
    </div>
</my:layout>