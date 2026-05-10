package es.uji.ei1027.sgovi.service;

import es.uji.ei1027.sgovi.dao.OviUserDao;
import es.uji.ei1027.sgovi.dao.PapPatiDao;
import es.uji.ei1027.sgovi.model.OviUser;
import es.uji.ei1027.sgovi.model.PapPati;
import es.uji.ei1027.sgovi.model.UserDetails;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionUserService {
    private static final String SESSION_USER = "user";

    @Autowired
    private OviUserDao oviUserDao;

    @Autowired
    private PapPatiDao papPatiDao;

    public UserDetails getCurrentUser(HttpSession session) {
        if (session == null) {
            return null;
        }

        Object value = session.getAttribute(SESSION_USER);
        return value instanceof UserDetails ? (UserDetails) value : null;
    }

    public boolean isLoggedIn(HttpSession session) {
        return getCurrentUser(session) != null;
    }

    public String getCurrentRole(HttpSession session) {
        UserDetails user = getCurrentUser(session);
        return user != null ? user.getRole() : null;
    }

    public boolean isTechnician(HttpSession session) {
        return "TECNICO".equals(getCurrentRole(session));
    }

    public boolean isOviUser(HttpSession session) {
        return "OVIUSER".equals(getCurrentRole(session));
    }

    public boolean isPapPati(HttpSession session) {
        return "PAPPATI".equals(getCurrentRole(session));
    }

    public OviUser getCurrentOviUser(HttpSession session) {
        UserDetails currentUser = getCurrentUser(session);
        if (currentUser == null || !"OVIUSER".equals(currentUser.getRole())) {
            return null;
        }
        return oviUserDao.getByEmail(currentUser.getEmail());
    }

    public PapPati getCurrentPapPati(HttpSession session) {
        UserDetails currentUser = getCurrentUser(session);
        if (currentUser == null || !"PAPPATI".equals(currentUser.getRole())) {
            return null;
        }
        return papPatiDao.getByEmail(currentUser.getEmail());
    }

    public Integer getCurrentOviUserId(HttpSession session) {
        OviUser oviUser = getCurrentOviUser(session);
        return oviUser != null ? oviUser.getIdOviUser() : null;
    }

    public Integer getCurrentPapPatiId(HttpSession session) {
        PapPati papPati = getCurrentPapPati(session);
        return papPati != null ? papPati.getIdPapPati() : null;
    }
}

