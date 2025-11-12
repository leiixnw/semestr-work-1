<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="my" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<my:layout title="Все поездки">
    <my:nav/>

    <h1>Все поездки</h1>

    <form method="get" class="filter-form">
        <div class="form-group">
            <input type="text" name="destination" placeholder="Куда?" value="${param.destination}">
        </div>
        <button type="submit" class="btn">Найти</button>
    </form>

<%--    <div class="trip-list">--%>
<%--        <c:forEach items="${trips}" var="trip">--%>
<%--            <my:tripCard trip="${trip}"/>--%>
<%--        </c:forEach>--%>
<%--    </div>--%>
</my:layout>