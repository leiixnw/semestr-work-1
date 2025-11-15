<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="my" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<my:layout title="Мои заявки">
    <my:nav/>

    <h1>Мои заявки на поездки</h1>

    <c:if test="${not empty param.success}">
        <div class="alert alert-success">${param.success}</div>
    </c:if>

    <c:if test="${not empty param.error}">
        <div class="alert alert-error">${param.error}</div>
    </c:if>

    <div class="trip-list">
        <c:forEach items="${myApplications}" var="app">
            <div class="card">
                <h3>${app.tripTitle}</h3>
                <p><strong>Направление:</strong> ${app.tripDestination}</p>
                <p><strong>Даты:</strong>
                        ${trip.formattedStartDate} - ${trip.formattedEndDate}
                </p>
                <p class="status-${app.status.toString().toLowerCase()}">
                    <strong>Статус заявки:</strong>
                    <c:choose>
                        <c:when test="${app.status == 'PENDING'}">Ожидает рассмотрения</c:when>
                        <c:when test="${app.status == 'ACCEPTED'}">Принята</c:when>
                        <c:when test="${app.status == 'REJECTED'}">Отклонена</c:when>
                    </c:choose>
                </p>
                <p><strong>Подана:</strong>
                        ${app.formattedAppliedAt}
                </p>

                <c:if test="${not empty app.message}">
                    <p><strong>Мое сообщение:</strong> ${app.message}</p>
                </c:if>

                <div class="action-buttons">
                    <a href="${pageContext.request.contextPath}/trip?id=${app.tripId}"
                       class="btn">Посмотреть поездку</a>

                    <c:if test="${app.status == 'PENDING'}">
                        <form action="${pageContext.request.contextPath}/application/delete"
                              method="post" style="display:inline;">
                            <input type="hidden" name="id" value="${app.id}">
                            <button type="submit" class="btn btn-danger"
                                    onclick="return confirmAction('Отозвать заявку?')">
                                Отозвать заявку
                            </button>
                        </form>
                    </c:if>
                </div>
            </div>
        </c:forEach>

        <c:if test="${empty myApplications}">
            <div class="card">
                <p>У вас пока нет заявок на поездки.</p>
            </div>
        </c:if>
    </div>
</my:layout>