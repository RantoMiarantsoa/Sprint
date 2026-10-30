package alpha.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class SessionHandler {

    public static MapSession getMapSession(HttpServletRequest request) {

        HttpSession session = request.getSession();

        MapSession mapSession =
                (MapSession) session.getAttribute("mapSession");

        if (mapSession == null) {
            mapSession = new MapSession();
            session.setAttribute("mapSession", mapSession);
        }

        mapSession.setIdSession(session.getId());

        return mapSession;
 
    }
}