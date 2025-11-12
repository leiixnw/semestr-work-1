<%@tag description="Check if user is creator" pageEncoding="UTF-8" %>
<%@attribute name="trip" required="true" type="java.lang.Object" %>
<%@attribute name="user" required="true" type="java.lang.Object" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${not empty user and user.id == trip.creatorId}">
    <jsp:doBody/>
</c:if>