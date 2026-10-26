package alpha.utils;

import jakarta.servlet.http.HttpSession;
import java.util.*;

public class MapSession {

    // clé : nom de la méthode, valeur : session Tomcat
    private Map<String, Object> mapsession = new HashMap<>();

    public void put(String methodName, HttpSession session) {
        mapsession.put(methodName, session);
    }

    public HttpSession getSession(String methodName) {
        return (HttpSession) mapsession.get(methodName);
    }

    public boolean contains(String methodName) {
        return mapsession.containsKey(methodName);
    }

    public Set<String> getMethodNames() {
        return mapsession.keySet();
    }

    public Map<String, Object> getMapsession() { return mapsession; }

    public String getId(String methodName) {
    HttpSession session = getSession(methodName);

    if (session == null) {
        return null;
    }

    return session.getId();
}
}