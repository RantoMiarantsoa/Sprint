package alpha.utils;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import com.google.gson.Gson;
import org.springframework.context.ApplicationContext;
import alpha.omega.ApiRest;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class Execution extends HttpServlet {

    public static void executeMethode(Method method,
            HttpServletRequest req,
            HttpServletResponse res,
            ApplicationContext applicationContext) {
        try {
            Class<?> clazz = method.getDeclaringClass();
            Object object = clazz.getDeclaredConstructor().newInstance();

            Parameter[] params = method.getParameters();
            Object[] arguments = new Object[params.length];


            for (int i = 0; i < params.length; i++) {
                String value = req.getParameter(params[i].getName());
                Class<?> type = params[i].getType();

                if (type == int.class) {
                    arguments[i] = Integer.parseInt(value);
                } else if (type == double.class) {
                    arguments[i] = Double.parseDouble(value);
                } else if (type == boolean.class) {
                    arguments[i] = Boolean.parseBoolean(value);
                } else if (type == long.class) {
                    arguments[i] = Long.parseLong(value);
                } else if (type == String.class) {
                    arguments[i] = value;
                }

System.out.println(arguments[i]);
            }

            Object retour = method.invoke(object, arguments);
           

            PrintWriter out = res.getWriter();

            for (Object argument : arguments) {
    out.println(argument);
}

            if (retour instanceof ModelAndView modelAndView) {

                Dispatcher.dispatch(modelAndView, req, res);

            } else if (method.isAnnotationPresent(ApiRest.class)) {

                if (retour instanceof String str) {
                    out.println(str);
                } else {
                    Gson gson = new Gson();
                    out.println(gson.toJson(retour));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Object resolveArgument(Parameter parameter,
            HttpServletRequest req,
            HttpServletResponse res,
            ApplicationContext applicationContext) {
        Class<?> type = parameter.getType();
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
