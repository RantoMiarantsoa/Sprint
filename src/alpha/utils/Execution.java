package alpha.utils;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import com.google.gson.Gson;
import org.springframework.context.ApplicationContext;
import alpha.omega.ApiRest;
import alpha.omega.ModelAtrribute;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.InputStream;

import java.lang.reflect.Field;

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
               if(params[i].isAnnotationPresent(ModelAtrribute.class)){
                    arguments[i] = bindObject(type, req);
               }else{
                if(value == null){
                    throw new RuntimeException("Parametre manquant: " +params[i].getName());
                }
                convert(value, type);
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

 private static Object bindObject(
        Class<?> type,
        HttpServletRequest req) throws Exception {

    Object obj = type.getDeclaredConstructor().newInstance();

    for (Field f : type.getDeclaredFields()) {
f.setAccessible(true);
if(f.getType() == MapFile.class){
    Part part = req.getPart(f.getName());
    if(part == null || part.getSize() == 0){
        continue;
    }
    f.set(obj,toMapFile(part));
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

public static Object convert(String value,Class<?> type){
     if(type == String.class)
     return value;
     if(type == int.class || type == Integer.class)
        return  Integer.parseInt(value);
    if(type == double.class || type == Double.class)
        return Double.parseDouble(value);
    if(type == boolean.class || type == Boolean.class)
        return Boolean.parseBoolean(value);
    if( type == long.class || type == Long.class)
        return Long.parseLong(value);
    throw new IllegalArgumentException("Type inexistant :" + type.getName());
}

public static Object toMapFile(Part part) throws Exception{
    String submitted = part.getSubmittedFileName();
    String nom = submitted;
    String extension = "";
    int dot = submitted.lastIndexOf('.');
    if(dot > 0){
        nom = submitted.substring(0,dot);
        extension = submitted.substring(1,dot);
    }
    try(InputStream in = part.getInputStream()){
        return new MapFile(nom, extension, in.readAllBytes());
    }
}
}
