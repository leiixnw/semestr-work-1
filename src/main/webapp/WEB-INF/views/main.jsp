<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="my" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<my:layout title="TravelBuddy - Главная">
    <my:nav/>
    <h1>Добро пожаловать в TravelBuddy!</h1>
    <p>Найди попутчиков для путешествий или создай свою поездку.</p>

    <c:if test="${not empty sessionScope.user}">
        <a href="${pageContext.request.contextPath}/trip/create" class="btn">Создать поездку</a>
    </c:if>

    <h2>Последние поездки</h2>

    <div class="trip-list">
        <c:forEach items="${recentTrips}" var="trip">
            <div class="trip-item">
                <h3>${trip.title}</h3>
                <p><strong>Куда:</strong> ${trip.destination}</p>
                <p><strong>Когда:</strong>
                        ${trip.formattedStartDate} - ${trip.formattedEndDate}
                </p>
                <p><strong>Участники:</strong> ${trip.currentParticipants}/${trip.maxParticipants}</p>

                <my:ifCreator trip="${trip}" user="${sessionScope.user}">
                    <span class="badge">Ваша поездка</span>
                </my:ifCreator>

                <a href="${pageContext.request.contextPath}/trip?id=${trip.id}" class="btn">Подробнее</a>
            </div>
        </c:forEach>
    </div>
</my:layout>