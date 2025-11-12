<%@tag description="Check if user is authenticated" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${not empty sessionScope.user}">
    <jsp:doBody/>
</c:if>