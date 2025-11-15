<%@tag description="trip card component" pageEncoding="UTF-8" %>
<%@attribute name="trip" required="true" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="my" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="trip-item">
    <h3>${trip.title}</h3>
    <div class="trip-meta">
        <strong>Направление:</strong> ${trip.destination}<br>
        <strong>Даты:</strong>
        ${trip.formattedStartDate} - ${trip.formattedEndDate}
        <br>
        <strong>Участники:</strong> ${trip.currentParticipants}/${trip.maxParticipants}
    </div>
    <p>${fn:substring(trip.description, 0, 100)}...</p>

    <my:ifCreator trip="${trip}" user="${sessionScope.user}">
        <span class="badge">Ваша поездка</span>
    </my:ifCreator>

    <a href="${pageContext.request.contextPath}/trip?id=${trip.id}" class="btn">Подробнее</a>
</div>