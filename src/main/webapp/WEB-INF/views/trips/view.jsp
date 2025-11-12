<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="my" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<my:layout title="${trip.title}">
    <div class="nav">
        <a href="${pageContext.request.contextPath}/main">← Назад к списку</a>
    </div>

    <div class="card">
        <h1>${trip.title}</h1>

        <div class="trip-meta">
            <p><strong>📍 Направление:</strong> ${trip.destination}</p>
            <p><strong>📅 Даты:</strong>
                    ${trip.formattedStartDate} - ${trip.formattedEndDate}
            </p>
            <p><strong>👥 Участники:</strong> ${trip.currentParticipants}/${trip.maxParticipants}</p>
            <p><strong>👤 Организатор:</strong> ${trip.creatorUsername}</p>
        </div>

        <div class="card">
            <h3>Описание поездки</h3>
            <p>${trip.description}</p>
        </div>

        <div class="action-buttons">
            <my:ifAuthenticated>
                <c:choose>
                    <c:when test="${sessionScope.user.id == trip.creatorId}">
                        <a href="${pageContext.request.contextPath}/trip/edit?id=${trip.id}" class="btn">Редактировать</a>
                        <form action="${pageContext.request.contextPath}/trip/delete" method="post" style="display:inline;">
                            <input type="hidden" name="id" value="${trip.id}">
                            <button type="submit" class="btn btn-danger"
                                    onclick="return confirmAction('Удалить эту поездку?')">Удалить</button>
                        </form>
                    </c:when>
                    <c:when test="${not empty userApplication && userApplication.present}">
                        <p class="status-${userApplication.get().status.toString().toLowerCase()}">
                            Статус вашей заявки: ${userApplication.get().status}
                        </p>
                    </c:when>
                    <c:otherwise>
                        <button onclick="toggleApplicationForm()" class="btn btn-success">Подать заявку</button>
                    </c:otherwise>
                </c:choose>
            </my:ifAuthenticated>
        </div>
    </div>

    <div id="applicationForm" style="display:none;" class="card">
        <h3>Подать заявку на участие</h3>
        <form action="${pageContext.request.contextPath}/application/create" method="post">
            <input type="hidden" name="tripId" value="${trip.id}">
            <div class="form-group">
                <label>Сообщение организатору (необязательно):</label>
                <textarea name="message" rows="4" placeholder="Расскажите о себе..."></textarea>
            </div>
            <button type="submit" class="btn">Отправить заявку</button>
            <button type="button" class="btn btn-danger" onclick="toggleApplicationForm()">Отмена</button>
        </form>
    </div>

    <my:ifCreator trip="${trip}" user="${sessionScope.user}">
        <div class="card">
            <h3>Заявки на участие</h3>
            <c:forEach items="${applications}" var="app">
                <div class="trip-item">
                    <h4>${app.applicantUsername}</h4>
                    <p class="status-${app.status.toString().toLowerCase()}">Статус: ${app.status}</p>
                    <p class="trip-meta">Подана: ${app.formattedAppliedAt}</p>

                    <c:if test="${not empty app.message}">
                        <p><strong>Сообщение:</strong> ${app.message}</p>
                    </c:if>

                    <c:if test="${app.status == 'PENDING'}">
                        <form action="${pageContext.request.contextPath}/application/update-status" method="post" style="display:inline;">
                            <input type="hidden" name="applicationId" value="${app.id}">
                            <input type="hidden" name="tripId" value="${trip.id}">
                            <input type="hidden" name="status" value="ACCEPTED">
                            <button type="submit" class="btn btn-success">Принять</button>
                        </form>
                        <form action="${pageContext.request.contextPath}/application/update-status" method="post" style="display:inline;">
                            <input type="hidden" name="applicationId" value="${app.id}">
                            <input type="hidden" name="tripId" value="${trip.id}">
                            <input type="hidden" name="status" value="REJECTED">
                            <button type="submit" class="btn btn-danger">Отклонить</button>
                        </form>
                    </c:if>
                </div>
            </c:forEach>
        </div>
    </my:ifCreator>
</my:layout>