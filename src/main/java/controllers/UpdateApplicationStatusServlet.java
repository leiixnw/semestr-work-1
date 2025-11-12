package controllers;

import entities.User;
import enums.Status;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import services.ApplicationServiceImpl;
import services.TripServiceImpl;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/application/update-status")
public class UpdateApplicationStatusServlet extends HttpServlet {

    private ApplicationServiceImpl applicationService;
    private TripServiceImpl tripService;

    @Override
    public void init() throws ServletException {
        applicationService = (ApplicationServiceImpl) getServletContext().getAttribute("applicationService");
        tripService = (TripServiceImpl) getServletContext().getAttribute("tripService");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            Long applicationId = Long.parseLong(request.getParameter("applicationId"));
            Status status = Status.valueOf(request.getParameter("status"));
            Long tripId = Long.parseLong(request.getParameter("tripId"));

            Optional<entities.Trip> tripOpt = tripService.findById(tripId);
            if (tripOpt.isEmpty() || !tripOpt.get().getCreatorId().equals(user.getId())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "У вас нет прав для управления заявками этой поездки");
                return;
            }

            applicationService.updateApplicationStatus(applicationId, status, user.getId());
            response.sendRedirect(request.getContextPath() + "/trip?id=" + tripId + "&success=Статус заявки обновлен");
        } catch (IllegalArgumentException e) {
            response.sendRedirect(request.getContextPath() + "/trip?id=" + request.getParameter("tripId") + "&error=" + e.getMessage());
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/trip?id=" + request.getParameter("tripId") + "&error=Ошибка обновления статуса");
        }
    }
}