package alpha.utils;

import jakarta.servlet.http.HttpSession;
import java.util.*;

public class MapSession {

    // clé : nom de la méthode, valeur : session Tomcat
    private Map<String, Object> mapsession = new HashMap<>();
private String idSession;

public void setIdSession(String idSession) {
    this.idSession = idSession;
}

public String getIdSession() {
    return idSession;
}
    public void put(String key, HttpSession session) {
        mapsession.put(key, session);
    }

    public boolean contains(String key) {
        return mapsession.containsKey(key);
    }

    public Set<String> getkeys() {
        return mapsession.keySet();
    }

    public Map<String, Object> getMapsession() { return mapsession; }
    public void ajouter(String key, Object value) {
        mapsession.put(key, value);
        
    }

    public void delete(String key) {
        mapsession.remove(key);
    }

    public Object get(String key) {
        return mapsession.get(key);
    }

    
}