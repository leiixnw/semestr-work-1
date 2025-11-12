<%@ tag pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<nav class="nav">
    <a href="${pageContext.request.contextPath}/main">Главная</a>

    <c:choose>
        <c:when test="${not empty sessionScope.user}">
            <a href="${pageContext.request.contextPath}/my-trips">Мои поездки</a>
            <a href="${pageContext.request.contextPath}/my-applications">Мои заявки</a>
            <a href="${pageContext.request.contextPath}/logout">Выйти (${sessionScope.user.username})</a>
        </c:when>
        <c:otherwise>
            <a href="${pageContext.request.contextPath}/login">Войти</a>
            <a href="${pageContext.request.contextPath}/register">Регистрация</a>
        </c:otherwise>
    </c:choose>
</nav>