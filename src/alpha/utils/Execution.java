    package alpha.utils;

    import java.io.PrintWriter;
    import java.lang.reflect.Method;
    import java.util.Arrays;

    import com.google.gson.Gson;
    import org.springframework.context.ApplicationContext;
    import jakarta.servlet.http.HttpServlet;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;
    import jakarta.servlet.RequestDispatcher;
    import jakarta.servlet.ServletException;

    public class Execution extends HttpServlet {
     
        
        public static void executeMethode(Method method,
        HttpServletRequest req,
        HttpServletResponse res,
        ApplicationContext applicationContext) {
            try {
        Class<?> clazz = method.getDeclaringClass();
        Object object = clazz.getDeclaredConstructor().newInstance();

        Object[] arguments = Arrays.stream(method.getParameterTypes())
            .map(type -> resolveArgument(type, req, res, applicationContext))
            .toArray();

        Object retour = method.invoke(object, arguments);

        PrintWriter out = res.getWriter();

        if (retour instanceof String str) {

            out.println(str);

        } else if (retour instanceof ModelAndView modelAndView) {

            Dispatcher.dispatch(modelAndView, req, res);

        } else if (retour != null) {

            Gson gson = new Gson();

            String json = gson.toJson(retour);


            out.println(json);

        } else {

            out.println("Type de retour non pris en charge");
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}

    private static Object resolveArgument(Class<?> type,
            HttpServletRequest req,
            HttpServletResponse res,
            ApplicationContext applicationContext) {
        if (type == ApplicationContext.class) {
            return applicationContext;
        }
        if (type == HttpServletRequest.class) {
            return req;
        }
        if (type == HttpServletResponse.class) {
            return res;
        }
        if (applicationContext == null) {
            throw new IllegalStateException("Aucun contexte Spring disponible pour le parametre " + type.getName());
        }
        return applicationContext.getBean(type);
    }
}
