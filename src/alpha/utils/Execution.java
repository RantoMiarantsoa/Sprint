package alpha.utils;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

import com.google.gson.Gson;
import org.springframework.context.ApplicationContext;
import alpha.omega.ApiRest;
import alpha.omega.ModelAtrribute;
import alpha.omega.Auth;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;

import java.lang.reflect.Field;

public class Execution extends HttpServlet {

    public static void executeMethode(
            Method method,
            HttpServletRequest req,
            HttpServletResponse res,
            ApplicationContext applicationContext) {

        try {
            Class<?> clazz = method.getDeclaringClass();
            Object object = clazz.getDeclaredConstructor().newInstance();

            Object[] arguments = preparerArguments(method, req, res, applicationContext);
            if (method.isAnnotationPresent(Auth.class)) {

                String sessionKey = req.getServletContext()
                        .getInitParameter("session");

                if (sessionKey == null) {
                    sessionKey = "idsession";
                }
                
                HttpSession httpSession = req.getSession(false);
                
                MapSession mapSession = (httpSession == null)
                ? null
                : (MapSession) httpSession.getAttribute("mapSession");
                
                if (mapSession == null || mapSession.get(sessionKey) == null) {
                    res.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Authentification requise");
                        return;
                    }
                   String[] role = method.getAnnotation(Auth.class).value();
                    if(role.length > 0){
                        Object roleUtilisateur = mapSession.get("role");

                        if(!verifierRole(role, roleUtilisateur)){
                             res.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès refusé : rôle insuffisant");
    return;
                        }
                    }
            }

            Object retour = method.invoke(object, arguments);

            if (retour instanceof ModelAndView modelAndView) {
                Dispatcher.dispatch(modelAndView, req, res);

            } else if (method.isAnnotationPresent(ApiRest.class)) {
                envoyerReponse(retour, res);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(
                    "Erreur lors de l'exécution de la méthode : " + e.getMessage());
        }
    }

    private static Object[] preparerArguments(Method method, HttpServletRequest req,
            HttpServletResponse res, ApplicationContext ctx) throws Exception {

        Parameter[] params = method.getParameters();
        Object[] arguments = new Object[params.length];

        for (int i = 0; i < params.length; i++) {
            Parameter param = params[i];
            Class<?> type = param.getType();

            if (type == MapSession.class) {
                arguments[i] = SessionHandler.getMapSession(req);
            } else if (type == HttpServletRequest.class) {
                arguments[i] = req;
            } else if (type == HttpServletResponse.class) {
                arguments[i] = res;
            } else if (type == ApplicationContext.class) {
                arguments[i] = ctx;
            } else if (param.isAnnotationPresent(ModelAtrribute.class)) {
                arguments[i] = bindObject(type, req);
            } else {
                String value = req.getParameter(param.getName());
                if (value == null) {
                    throw new IllegalArgumentException("Paramètre manquant : " + param.getName());
                }
                arguments[i] = convert(value, type);
            }
        }
        return arguments;
    }
public static boolean verifierRole(String[] roleRequis,Object roleUtilisateur){
    if(roleRequis.length == 0){
        return true;
    }
    if(roleUtilisateur == null){
        return false;
    }
return Arrays.asList(roleRequis).contains(roleUtilisateur.toString());
}
    private static void envoyerReponse(
            Object retour,
            HttpServletResponse res) throws Exception {

        PrintWriter out = res.getWriter();

        if (retour instanceof String str) {
            out.println(str);
        } else {
            out.println(new Gson().toJson(retour));
        }
    }

    private static Object bindObject(
            Class<?> type,
            HttpServletRequest req) throws Exception {

        Object obj = type.getDeclaredConstructor().newInstance();

        for (Field f : type.getDeclaredFields()) {
            f.setAccessible(true);
            if (f.getType() == MapFile.class) {
                Part part = req.getPart(f.getName());
                if (part == null || part.getSize() == 0) {
                    continue;
                }
                f.set(obj, toMapFile(part));
            }
            String value = req.getParameter(f.getName());

            if (value == null) {
                continue;
            }

            f.setAccessible(true);

            f.set(obj, convert(value, f.getType()));
        }

        return obj;
    }

    public static Object convert(String value, Class<?> type) {
        if (type == String.class)
            return value;
        if (type == int.class || type == Integer.class)
            return Integer.parseInt(value);
        if (type == double.class || type == Double.class)
            return Double.parseDouble(value);
        if (type == boolean.class || type == Boolean.class)
            return Boolean.parseBoolean(value);
        if (type == long.class || type == Long.class)
            return Long.parseLong(value);
        throw new IllegalArgumentException("Type inexistant :" + type.getName());
    }

    public static Object toMapFile(Part part) throws Exception {
        String submitted = part.getSubmittedFileName();
        String nom = submitted;
        String extension = "";
        int dot = submitted.lastIndexOf('.');
        if (dot > 0) {
            nom = submitted.substring(0, dot);
            extension = submitted.substring(1, dot);
        }
        try (InputStream in = part.getInputStream()) {
            return new MapFile(nom, extension, in.readAllBytes());
        }
    }

}
