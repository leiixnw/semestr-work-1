package listeners;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import repositories.*;
import services.*;


@WebListener
public class InitListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        UserRepositoryImpl userRepository = new UserRepositoryImpl();
        TripRepositoryImpl tripRepository = new TripRepositoryImpl();
        ApplicationRepositoryImpl applicationRepository = new ApplicationRepositoryImpl();
        SessionRepositoryImpl sessionRepository = new SessionRepositoryImpl();

        SecurityServiceImpl securityService = new SecurityServiceImpl(userRepository, sessionRepository);
        UserServiceImpl userService = new UserServiceImpl(securityService, userRepository);
        TripServiceImpl tripService = new TripServiceImpl(tripRepository);
        ApplicationServiceImpl applicationService = new ApplicationServiceImpl(applicationRepository, tripRepository);

        servletContextEvent.getServletContext().setAttribute("securityService", securityService);
        servletContextEvent.getServletContext().setAttribute("userService",  userService);
        servletContextEvent.getServletContext().setAttribute("applicationService", applicationService);
        servletContextEvent.getServletContext().setAttribute("tripService", tripService);
    }
}
