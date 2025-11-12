package controllers;

import entities.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import services.TripServiceImpl;

import java.io.IOException;

@WebServlet("/trip/delete")
public class DeleteTripServlet extends HttpServlet {

    private TripServiceImpl tripService;

    @Override
    public void init() throws ServletException {
        tripService = (TripServiceImpl) getServletContext().getAttribute("tripService");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
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
            Long tripId = Long.parseLong(request.getParameter("id"));
            boolean deleted = tripService.deleteTrip(tripId, user.getId());

            if (deleted) {
                response.sendRedirect(request.getContextPath() + "/main?success=Поездка удалена");
            } else {
                response.sendRedirect(request.getContextPath() + "/main?error=Не удалось удалить поездку");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Неверный ID поездки");
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/main?error=" + e.getMessage());
        }
    }
}