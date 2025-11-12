//package controllers;
//
//import entities.Trip;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import services.TripService;
//import java.io.IOException;
//import java.util.List;
//import java.util.Map;
//
//@WebServlet("/trips")
//public class TripListServlet extends HttpServlet {
//
//    private TripService tripService;
//
//    @Override
//    public void init() throws ServletException {
//        tripService = (TripService) getServletContext().getAttribute("tripService");
//    }
//
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        try {
//            String destination = request.getParameter("destination");
//            String status = request.getParameter("status");
//
//            List<Trip> trips = tripService.findAllTrips();
//            request.setAttribute("trips", trips);
//            request.setAttribute("filters", Map.of(
//                    "destination", destination != null ? destination : "",
//                    "status", status != null ? status : ""
//            ));
//
//            request.getRequestDispatcher("/WEB-INF/views/trips/list.jsp").forward(request, response);
//        } catch (Exception e) {
//            request.setAttribute("error", "Ошибка загрузки списка поездок: " + e.getMessage());
//            request.getRequestDispatcher("/WEB-INF/views/trips/list.jsp").forward(request, response);
//        }
//    }
//}