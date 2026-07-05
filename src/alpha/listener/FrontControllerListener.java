package alpha.listener;

import java.util.HashMap;
import java.util.List;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import alpha.omega.Controller;
import alpha.utils.RouteMapping;
import alpha.utils.UrlClasse;
import alpha.utils.Utils;

@WebListener
public class FrontControllerListener implements ServletContextListener {

    public static final String CONTROLLERS_ATTRIBUTE = "controllers";
    public static final String ROUTES_ATTRIBUTE = "routes";
    public static final String PACKAGE_NAME_ATTRIBUTE = "packageName";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            ServletContext context = sce.getServletContext();
            String packageName = context.getInitParameter(PACKAGE_NAME_ATTRIBUTE);

            if (packageName == null || packageName.isEmpty()) {
                throw new IllegalStateException("packageName context-param is required");
            }

            List<Class<?>> controllers = Utils.getNameAnnote(Controller.class, packageName);
            HashMap<UrlClasse, RouteMapping> routes = Utils.getMethodFunction(controllers);

            context.setAttribute(CONTROLLERS_ATTRIBUTE, controllers);
            context.setAttribute(ROUTES_ATTRIBUTE, routes);
            System.out.println("Listener initialized " + controllers.size() + " controller(s) for " + packageName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
