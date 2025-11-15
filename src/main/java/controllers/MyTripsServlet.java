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

@WebServlet("/my-trips")
public class MyTripsServlet extends HttpServlet {

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
            var myTrips = tripService.findTripsByCreator(user.getId());
            request.setAttribute("myTrips", myTrips);
            request.getRequestDispatcher("/WEB-INF/views/user/my-trips.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Ошибка загрузки ваших поездок: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/user/my-trips.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}