package alpha.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import alpha.utils.Dispatcher;
import alpha.utils.Execution;
import alpha.utils.ModelAndView;
import alpha.utils.RouteMapping;
import alpha.utils.UrlClasse;
import alpha.listener.FrontControllerListener;

public class FrontControllerServlet extends HttpServlet {
    private List<Class<?>> controllers;
    HashMap<UrlClasse, RouteMapping> mapMethod;
    

@Override
public void init() throws ServletException {
    super.init();

    ServletContext context = getServletContext();
    controllers = (List<Class<?>>) context.getAttribute(FrontControllerListener.CONTROLLERS_ATTRIBUTE);
    mapMethod = (HashMap<UrlClasse, RouteMapping>) context.getAttribute(FrontControllerListener.ROUTES_ATTRIBUTE);

 


    if (controllers == null || mapMethod == null) {
        throw new ServletException("FrontControllerListener must initialize controllers and routes before the servlet starts");
    }

    System.out.println("Controllers trouvés: " + controllers.size());
    controllers.forEach(c -> System.out.println("  - " + c.getName()));
}

    public RouteMapping getProcessPath(String requestMethod, String requestPath) {
        return mapMethod.get(new UrlClasse(requestMethod, requestPath));
    }

    public RouteMapping getProcessPathWithFallback(String requestMethod, String requestPath) {
        RouteMapping mapping = getProcessPath(requestMethod, requestPath);

        if (mapping == null && "POST".equalsIgnoreCase(requestMethod)) {
            mapping = getProcessPath("GET", requestPath);
        }

        return mapping;
    }

    protected void processRequest(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        res.setContentType("text/html;charset=UTF-8");
    PrintWriter out = res.getWriter();

    String servletPath = req.getServletPath();
    String requestMethod = req.getMethod();
    String requestPath = (servletPath == null || servletPath.isEmpty()) ? "/" : servletPath;
        
        RouteMapping mapping = getProcessPathWithFallback(requestMethod, requestPath);
        System.out.println("URL demandée: " + requestPath);
        System.out.println("Méthode HTTP: " + requestMethod);
        out.println("Request Path:" + requestPath);

        if (mapping != null) {
                try {
                    Class<?> controllerClass = mapping.getListeController();
                    Method method = mapping.getMethod();

                  Execution.executeMethode(method,req,res);
                System.out.println("Controller: " + controllerClass);
                System.out.println("Méthode: " + method.getName());
                out.println("Controller: " + controllerClass.getSimpleName() + "<br>");
                out.println("Méthode: " + method.getName() + "<br>");

            } catch (Exception e) {
                out.println("<h1> Erreur</h1>");
                out.println("<p>" + e.getMessage() + "</p>");
                e.printStackTrace();
            }
        } else {
            out.println("<html><body>");
            out.println("<h1>Routes disponibles:</h1>");
            out.println("<ul>");

            for (Map.Entry<UrlClasse, RouteMapping> entry : mapMethod.entrySet()) {
                String url = entry.getKey().getUrl();
                String className = entry.getValue().getListeController().getSimpleName();
                String method = entry.getValue().getMethod().getName();
                String parameterString = entry.getKey().getMethodehttp();
                out.println(
                        "<li>" + className + " | " + url + " | " + method + " | " + parameterString + "|" + "</li>");
            }

            out.println("</ul>");
            out.println("</body></html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        processRequest(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        doGet(req, res);
    }
}
