package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import services.SecurityServiceImpl;
import exceptions.AuthenticationException;

import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private SecurityServiceImpl  securityService;

    public void init() {
        this.securityService = (SecurityServiceImpl) getServletContext().getAttribute("securityService");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/user/register.jsp").forward(request, response);    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String passwordRepeat = request.getParameter("passwordRepeat");
        String sessionId;
        try {
            sessionId = securityService.registerUser(email, username, password, passwordRepeat);
            var userOpt = securityService.getUser(sessionId);
            if (userOpt.isPresent()) {
                HttpSession session = request.getSession();
                session.setAttribute("user", userOpt.get());
            }

            Cookie cookie = new Cookie("sessionId", sessionId);
            cookie.setMaxAge(60 * 60 * 24 * 30);
            response.addCookie(cookie);

            response.sendRedirect(request.getContextPath() + "/main");        } catch (AuthenticationException e) {
            request.setAttribute("error", "Invalid username or password");
            request.getRequestDispatcher("/WEB-INF/views/user/register.jsp").forward(request, response);
        }
    }
}
