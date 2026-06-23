package alpha.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
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
        controllers = utils.getNameAnnote(Controller.class, packageName);
        
        System.out.println("Controllers trouvés dans " + packageName + ": " + controllers.size());
        for (Class<?> controller : controllers) {
            System.out.println("  - " + controller.getName());
        }
    }

    protected void processRequest(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        res.setContentType("text/html;charset=UTF-8");
        PrintWriter out = res.getWriter();
        
        out.println("<html><body>");
        out.println("<h1>Controllers trouvés:</h1>");
        out.println("<ul>");
        for (Class<?> controller : controllers) {
            out.println("<li>" + controller.getName() + "</li>");
        }
        out.println("</ul>");
        out.println("</body></html>");
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