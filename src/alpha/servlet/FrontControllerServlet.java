package alpha.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import alpha.omega.Controller;
import alpha.utils.RouteMapping;
import alpha.utils.Utils;

public class FrontControllerServlet extends HttpServlet {
    private Utils utils;
    private String packageName;

    private List<Class<?>> controllers; 
    
    HashMap<String,RouteMapping> mapMethod;


    @Override
    public void init() throws ServletException {
        super.init();
        
        packageName = getInitParameter("packageName");
        
        if (packageName == null || packageName.isEmpty()) {
            throw new ServletException("packageName init parameter is required");
        }
        
        utils = new Utils();
        
        // ← Sauvegarder la liste 
        controllers = Utils.getNameAnnote(Controller.class, packageName);
        
        mapMethod = Utils.getMethodFunction(controllers);
        System.out.println("Controllers trouvés dans " + packageName + ": " + controllers.size());
        for (Class<?> controller : controllers) {
            System.out.println("  - " + controller.getName());
        }
    }

protected void processRequest(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
    res.setContentType("text/html;charset=UTF-8");
    PrintWriter out = res.getWriter();
    
    String pathInfo = req.getPathInfo();
    String requestPath = pathInfo != null ? pathInfo : "/";
    
    System.out.println("URL demandée: " + requestPath);
    
    if (mapMethod.containsKey(requestPath)) {
       
        RouteMapping mapping = mapMethod.get(requestPath);
        System.out.println("Route trouvée: " + requestPath);
        
        try {
       Class<?> controllerClass = mapping.getListeController();
Method method = mapping.getMethod();
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
    
    for (Map.Entry<String, RouteMapping> entry : mapMethod.entrySet()) {
        String url = entry.getKey();
        String className = entry.getValue().getListeController().getSimpleName();
        String method = entry.getValue().getMethod().getName();
        
        out.println("<li>" + className + " | " + url + " | " + method + "</li>");
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
