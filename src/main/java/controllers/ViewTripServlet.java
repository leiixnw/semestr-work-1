package controllers;

import entities.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import services.TripService;
import services.ApplicationService;
import java.io.IOException;
import java.util.Optional;

@WebServlet("/trip")
public class ViewTripServlet extends HttpServlet {

    private TripService tripService;
    private ApplicationService applicationService;

    @Override
    public void init() throws ServletException {
        tripService = (TripService) getServletContext().getAttribute("tripService");
        applicationService = (ApplicationService) getServletContext().getAttribute("applicationService");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Long tripId = Long.parseLong(request.getParameter("id"));
            Optional<entities.Trip> tripOpt = tripService.findById(tripId);

            if (tripOpt.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Поездка не найдена");
                return;
            }

            entities.Trip trip = tripOpt.get();

            HttpSession session = request.getSession(false);
            if (session != null) {
                User currentUser = (User) session.getAttribute("user");
                if (currentUser != null) {
                    var userApplication = applicationService.findByTripIdAndApplicantId(tripId, currentUser.getId());
                    request.setAttribute("userApplication", userApplication);

                    if (currentUser.getId().equals(trip.getCreatorId())) {
                        var applications = applicationService.findByTripId(tripId);
                        request.setAttribute("applications", applications);
                    }
                }
            }

            request.setAttribute("trip", trip);
            request.getRequestDispatcher("/WEB-INF/views/trips/view.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Неверный ID поездки");
        } catch (Exception e) {
            request.setAttribute("error", "Ошибка загрузки поездки: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/trips/view.jsp").forward(request, response);
        }
    }
}