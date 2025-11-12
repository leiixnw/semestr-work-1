package controllers;

import entities.Trip;
import entities.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import services.TripServiceImpl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

@WebServlet("/trip/edit")
public class EditTripServlet extends HttpServlet {

    private TripServiceImpl tripService;

    @Override
    public void init() throws ServletException {
        tripService = (TripServiceImpl) getServletContext().getAttribute("tripService");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            Long tripId = Long.parseLong(request.getParameter("id"));
            Optional<Trip> tripOpt = tripService.findById(tripId);

            if (tripOpt.isEmpty()) {
                response.sendError(404, "Поездка не найдена");
                return;
            }

            Trip trip = tripOpt.get();

            if (!trip.getCreatorId().equals(user.getId())) {
                response.sendError(403, "У вас нет прав для редактирования этой поездки");
                return;
            }

            request.setAttribute("trip", trip);
            request.getRequestDispatcher("/WEB-INF/views/trips/edit.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(400, "Неверный ID поездки");
        } catch (Exception e) {
            request.setAttribute("error", "Ошибка загрузки поездки: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/trips/edit.jsp").forward(request, response);
        }
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
            Optional<Trip> existingTripOpt = tripService.findById(tripId);

            if (existingTripOpt.isEmpty()) {
                response.sendError(404, "Поездка не найдена");
                return;
            }

            Trip existingTrip = existingTripOpt.get();

            if (!existingTrip.getCreatorId().equals(user.getId())) {
                response.sendError(403, "У вас нет прав для редактирования этой поездки");
                return;
            }

            Trip trip = Trip.builder()
                    .id(tripId)
                    .title(request.getParameter("title"))
                    .description(request.getParameter("description"))
                    .destination(request.getParameter("destination"))
                    .startDate(LocalDate.parse(request.getParameter("startDate")))
                    .endDate(LocalDate.parse(request.getParameter("endDate")))
                    .maxParticipants(Integer.parseInt(request.getParameter("maxParticipants")))
                    .creatorId(user.getId())
                    .status(request.getParameter("status"))
                    .build();

            tripService.updateTrip(trip);
            response.sendRedirect(request.getContextPath() + "/trip?id=" + trip.getId());

        } catch (Exception e) {
            request.setAttribute("error", "Ошибка обновления поездки: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/trips/edit.jsp").forward(request, response);
        }
    }
}