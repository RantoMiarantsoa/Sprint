package alpha.utils;
import alpha.omega.UrlMapping;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
public class Utils {
    public static List<Class<?>> getNameClass(String packageName) {
    List<Class<?>> listeClass = new ArrayList<>();
    String packagepath = packageName.replaceAll("\\.", "/");
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    URL packageURL = classLoader.getResource(packagepath);

    if (packageURL != null && packageURL.getProtocol().equals("file")) {
        try {
            File packageDirectory = new File(packageURL.toURI());
            
            if (packageDirectory.isDirectory()) {
                File[] files = packageDirectory.listFiles();

                if (files != null) {
                    for (File file : files) {
                        if (file.isFile() && file.getName().endsWith(".class")) {
                            String className = file.getName()
                                .replaceAll("\\.class$", "");
                            String fullClassName = packageName + "." + className;
                            
                            try {
                                Class<?> clazz = classLoader.loadClass(fullClassName);
                                listeClass.add(clazz);
                                System.out.println("Classe: " + fullClassName);
                            } catch (ClassNotFoundException e) {
                                System.out.println("Erreur chargement: " + fullClassName);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Package non trouvé ou invalide");
            e.printStackTrace();
        }
    }
    
    return listeClass;
}



public static HashMap<UrlClasse, RouteMapping> getMethodFunction(List<Class<?>> listeController) {

    HashMap<UrlClasse, RouteMapping> listeMap = new HashMap<>();

   for(Class<?> clazz : listeController){
    Method [] method = clazz.getDeclaredMethods();
    for(Method meth: method){
        if(meth.isAnnotationPresent(UrlMapping.class)){
            UrlMapping urlMapping = meth.getAnnotation(UrlMapping.class);
            String url = urlMapping.value();
            UrlClasse urlClasse = new UrlClasse(urlMapping.parameter(), url);
            RouteMapping routeMapping = new RouteMapping(clazz,meth);
            if(!listeMap.containsKey(urlClasse)){
                listeMap.put(urlClasse, routeMapping);
            }
        }
        
    }
   }
    return listeMap;
}
public static List<Class<?>> getNameAnnote(Class<? extends Annotation> annotation, String packageName) {
    List<Class<?>> nameclasse = new ArrayList<>();
    List<Class<?>> listeClass = Utils.getNameClass(packageName);
    
    for (Class<?> clazz : listeClass) {
        if (clazz.isAnnotationPresent(annotation)) {
            nameclasse.add(clazz);
            System.out.println("Classe avec annotation: " + clazz.getName());
        }
    }
    
    return nameclasse;
}



}