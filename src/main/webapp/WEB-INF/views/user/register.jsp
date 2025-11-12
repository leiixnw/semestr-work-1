<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="my" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<my:layout title="Регистрация">
    <div class="nav">
        <a href="${pageContext.request.contextPath}/main">Главная</a>
    </div>

    <h1>Регистрация</h1>

    <c:if test="${not empty error}">
        <div class="alert alert-error">${error}</div>
    </c:if>

    <div class="card">
        <form action="${pageContext.request.contextPath}/register" method="post">
            <div class="form-group">
                <label>Email:</label>
                <input type="email" name="email" required>
            </div>

            <div class="form-group">
                <label>Имя пользователя:</label>
                <input type="text" name="username" required>
            </div>

            <div class="form-group">
                <label>Пароль:</label>
                <input type="password" name="password" required>
            </div>

            <div class="form-group">
                <label>Повторите пароль:</label>
                <input type="password" name="passwordRepeat" required>
            </div>

            <button type="submit" class="btn">Зарегистрироваться</button>
        </form>

        <p style="text-align: center; margin-top: 1rem;">
            Уже есть аккаунт? <a href="${pageContext.request.contextPath}/login">Войти</a>
        </p>
    </div>
</my:layout>