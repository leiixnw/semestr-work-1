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

@WebServlet("/application/delete")
public class DeleteApplicationServlet extends HttpServlet {

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
            Long applicationId = Long.parseLong(request.getParameter("id"));
            boolean deleted = applicationService.deleteApplication(applicationId, user.getId());

            if (deleted) {
                response.sendRedirect(request.getContextPath() + "/my-applications");
            } else {
                response.sendRedirect(request.getContextPath() + "/my-applications?error=Не удалось удалить заявку");
            }
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/my-applications?error=Ошибка удаления заявки");
        }
    }
}
