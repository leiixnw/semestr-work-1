package controllers;

import entities.Application;
import entities.User;
import enums.Status;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import services.ApplicationServiceImpl;

import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet("/application/create")
public class CreateApplicationServlet extends HttpServlet {

    private ApplicationServiceImpl applicationService;

    @Override
    public void init() throws ServletException {
        applicationService = (ApplicationServiceImpl) getServletContext().getAttribute("applicationService");
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
            Long tripId = Long.parseLong(request.getParameter("tripId"));
            String message = request.getParameter("message");

            Application application = Application.builder()
                    .tripId(tripId)
                    .applicantId(user.getId())
                    .status(Status.PENDING)
                    .appliedAt(LocalDateTime.now())
                    .build();

            Application createdApplication = applicationService.createApplication(application);
            response.sendRedirect(request.getContextPath() + "/trip?id=" + tripId + "&success=Заявка отправлена");
        } catch (IllegalArgumentException e) {
            response.sendRedirect(request.getContextPath() + "/trip?id=" + request.getParameter("tripId") + "&error=" + e.getMessage());
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/trip?id=" + request.getParameter("tripId") + "&error=Ошибка отправки заявки");
        }
    }
}