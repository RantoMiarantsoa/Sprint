package alpha.utils;

import java.io.IOException;
import java.util.Map;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class Dispatcher {

    public static void dispatch(ModelAndView mv,
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

      ServletContext context = request.getServletContext();
      String prefix = context.getInitParameter("prefix");
      String suffix = context.getInitParameter("suffix");

        String view = prefix + mv.getURL() + suffix;
        for (Map.Entry<String, Object> entry : mv.getAttributes().entrySet()) {
            request.setAttribute(entry.getKey(), entry.getValue());
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher(view);

        dispatcher.forward(request, response);
    }
}