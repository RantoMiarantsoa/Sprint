package alpha.utils;

import java.lang.reflect.Method;

public class Execution {
    public static void executeMethode(Method method){
        Class<?> clazz = method.getDeclaringClass();
try{

    Object object = clazz.getDeclaredConstructor().newInstance();
    method.invoke(object);
}catch(Exception e){
    e.printStackTrace();
}
    }
}
