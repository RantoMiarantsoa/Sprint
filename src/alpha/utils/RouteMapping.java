package alpha.utils;

import java.lang.reflect.Method;

public class RouteMapping {
    Class<?> listeController;
    
    Method method;

    public RouteMapping(Class<?> listeController, Method method) {
        this.listeController = listeController;
        this.method = method;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }


    public Class<?> getListeController() {
        return listeController;
    }
    public void setListeController(Class<?> listeController) {
        this.listeController = listeController;
    }
}
