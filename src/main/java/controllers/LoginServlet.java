package controllers;

import entities.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import services.SecurityServiceImpl;
import exceptions.AuthenticationException;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private SecurityServiceImpl securityService;

    public void init()  throws ServletException {
        this.securityService = (SecurityServiceImpl) getServletContext().getAttribute("securityService");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/user/login.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email =  request.getParameter("email");
        String password = request.getParameter("password");
        String sessionId;

        try {
            sessionId = securityService.loginUser(email, password);
            Optional<User> userOpt = securityService.getUser(sessionId);
            if (userOpt.isPresent()) {
                HttpSession session = request.getSession();
                session.setAttribute("user", userOpt.get());
            }

            Cookie cookie = new Cookie("sessionId", sessionId);
            cookie.setMaxAge(60 * 60 * 24 * 30);
            response.addCookie(cookie);

            response.sendRedirect(request.getContextPath() + "/main");
        } catch (AuthenticationException e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/user/login.jsp").forward(request, response);
        }
    }
}
