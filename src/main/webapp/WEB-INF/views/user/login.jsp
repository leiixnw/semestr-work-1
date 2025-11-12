<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="my" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<my:layout title="Вход в систему">
    <div class="nav">
        <a href="${pageContext.request.contextPath}/main">Главная</a>
    </div>

    <h1>Вход в систему</h1>

    <c:if test="${not empty error}">
        <div class="alert alert-error">${error}</div>
    </c:if>

    <div class="card">
        <form action="${pageContext.request.contextPath}/login" method="post">
            <div class="form-group">
                <label>Email:</label>
                <input type="email" name="email" required>
            </div>

            <div class="form-group">
                <label>Пароль:</label>
                <input type="password" name="password" required>
            </div>

            <button type="submit" class="btn">Войти</button>
        </form>

        <p style="text-align: center; margin-top: 1rem;">
            Нет аккаунта? <a href="${pageContext.request.contextPath}/register">Зарегистрируйтесь</a>
        </p>
    </div>
</my:layout>