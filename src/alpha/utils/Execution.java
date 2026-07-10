    package alpha.utils;

    import java.lang.reflect.Method;

    import jakarta.servlet.http.HttpServlet;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;
    import jakarta.servlet.RequestDispatcher;
    import jakarta.servlet.ServletException;

    public class Execution extends HttpServlet {
     
        
        public static void executeMethode(Method method, HttpServletRequest req,
                                HttpServletResponse res) {
            try {
                Class<?> clazz = method.getDeclaringClass();
                Object object = clazz.getDeclaredConstructor().newInstance();
        
                Object retour = method.invoke(object);
        
                if (retour instanceof ModelAndView modelAndView) {
                  Dispatcher.dispatch(modelAndView, req, res);
                 System.out.println("Mety");
                        return;  
                }
        
            } catch (Exception e) {
                e.printStackTrace();
            }
        
            
        }


     
    }
