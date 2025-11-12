package controllers;

import entities.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import services.ApplicationServiceImpl;

import java.io.IOException;

@WebServlet("/my-applications")
public class MyApplicationsServlet extends HttpServlet {

    private ApplicationServiceImpl applicationService;

    @Override
    public void init() throws ServletException {
        applicationService = (ApplicationServiceImpl) getServletContext().getAttribute("applicationService");
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
            var myApplications = applicationService.findByApplicantId(user.getId());
            request.setAttribute("myApplications", myApplications);
            request.getRequestDispatcher("/WEB-INF/views/user/my-applications.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Ошибка загрузки ваших заявок: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/user/my-applications.jsp").forward(request, response);
        }
    }
}
