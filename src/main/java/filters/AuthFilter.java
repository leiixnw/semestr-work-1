package filters;

import entities.User;
import exceptions.AuthenticationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.SecurityServiceImpl;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebFilter(urlPatterns = "*")
public class AuthFilter extends HttpFilter {

    private final List<String> protectedPaths;
    private SecurityServiceImpl securityService;

    public AuthFilter() {
        this.protectedPaths = List.of("/profile");
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
        this.securityService = (SecurityServiceImpl) config.getServletContext().getAttribute("securityService");
    }

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        if (!protectedPaths.contains(req.getServletPath())) {
            chain.doFilter(req, res);
            return;
        }
        String sessionId = extractSessionId(req.getCookies());
        if (sessionId == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        Optional<User> user;
        try {
            user = securityService.getUser(sessionId);
        } catch (AuthenticationException e) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        req.setAttribute("user", user);
        chain.doFilter(req, res);
    }

    private String extractSessionId(Cookie[] cookies) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("sessionId")) {
                return cookie.getValue();
            }
        }
        return null;
    }
}