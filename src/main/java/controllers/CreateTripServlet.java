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

@WebServlet("/trip/create")
public class CreateTripServlet extends HttpServlet {

    private TripServiceImpl tripService;

    @Override
    public void init() throws ServletException {
        tripService = (TripServiceImpl) getServletContext().getAttribute("tripService");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/trips/create.jsp").forward(request, response);
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
            Trip trip = Trip.builder()
                    .title(request.getParameter("title"))
                    .description(request.getParameter("description"))
                    .destination(request.getParameter("destination"))
                    .startDate(LocalDate.parse(request.getParameter("startDate")))
                    .endDate(LocalDate.parse(request.getParameter("endDate")))
                    .maxParticipants(Integer.parseInt(request.getParameter("maxParticipants")))
                    .creatorId(user.getId())
                    .status("ACTIVE")
                    .build();

            if (trip.getStartDate().isAfter(trip.getEndDate())) {
                request.setAttribute("error", "Дата начала не может быть позже даты окончания");
                request.getRequestDispatcher("/WEB-INF/views/trips/create.jsp").forward(request, response);
                return;
            }

            Trip createdTrip = tripService.createTrip(trip);
            response.sendRedirect(request.getContextPath() + "/trip?id=" + createdTrip.getId());

        } catch (Exception e) {
            request.setAttribute("error", "Ошибка создания поездки: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/trips/create.jsp").forward(request, response);
        }
    }
}