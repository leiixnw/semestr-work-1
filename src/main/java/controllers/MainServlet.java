package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.TripService;
import java.io.IOException;

@WebServlet("/main")
public class MainServlet extends HttpServlet {

    private TripService tripService;

    @Override
    public void init() throws ServletException {
        tripService = (TripService) getServletContext().getAttribute("tripService");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            var recentTrips = tripService.findActiveTrips();
            request.setAttribute("recentTrips", recentTrips);
            request.getRequestDispatcher("/WEB-INF/views/main.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Ошибка загрузки данных: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/main.jsp").forward(request, response);
        }
    }
}