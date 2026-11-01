package alpha.utils;

import jakarta.servlet.ServletContext;

public class Singleton {

    private static Singleton singleInstance = null;

    private Singleton() { }

    public static synchronized Singleton getInstance() {
        if (singleInstance == null) {
            singleInstance = new Singleton();
        }
        return singleInstance;
    }

    public synchronized Object getControleur(Class<?> clazz, ServletContext context) throws Exception {
        String key = "controleur:" + clazz.getName();

        Object controleur = context.getAttribute(key);

        if (controleur == null) {
            controleur = clazz.getDeclaredConstructor().newInstance();
            context.setAttribute(key, controleur);   // stocké dans le contexte
        }

        return controleur;
    }
}