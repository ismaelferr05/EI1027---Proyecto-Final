package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.OviUser;
import es.uji.ei1027.sgovi.model.PapPati;
import es.uji.ei1027.sgovi.model.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
public class DatabaseUserDao implements UserDao {
    @Autowired
    private OviUserDao oviUserDao;

    @Autowired
    private PapPatiDao papPatiDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public UserDetails getUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) return null;
        String normalized = email.trim();

        OviUser o = oviUserDao.getByEmail(normalized);
        if (o != null) return mapOviUser(o);

        PapPati p = papPatiDao.getByEmail(normalized);
        if (p != null) return mapPapPati(p);

        // Technician table: only email/password are needed here
        String sql = "SELECT email, password FROM Technician WHERE LOWER(email)=LOWER(?)";
        List<UserDetails> techs = jdbcTemplate.query(sql, (rs, rowNum) -> {
            UserDetails u = new UserDetails();
            u.setEmail(rs.getString("email"));
            u.setPassword(rs.getString("password"));
            u.setRole("TECNICO");
            return u;
        }, normalized);
        return techs.isEmpty() ? null : techs.get(0);
    }

    @Override
    public Collection<UserDetails> getAllUsers() {
        List<UserDetails> users = new ArrayList<>();

        List<OviUser> oviUsers = oviUserDao.getAll();
        for (OviUser o : oviUsers) {
            users.add(mapOviUser(o));
        }

        List<PapPati> papPatis = papPatiDao.getAll();
        for (PapPati p : papPatis) {
            users.add(mapPapPati(p));
        }

        users.addAll(jdbcTemplate.query("SELECT email, password FROM Technician", (rs, rowNum) -> {
            UserDetails u = new UserDetails();
            u.setEmail(rs.getString("email"));
            u.setPassword(rs.getString("password"));
            u.setRole("TECNICO");
            return u;
        }));

        return users;
    }

    private UserDetails mapOviUser(OviUser o) {
        UserDetails u = new UserDetails();
        u.setEmail(o.getEmail());
        u.setPassword(o.getPassword());
        u.setRole("OVIUSER");
        return u;
    }

    private UserDetails mapPapPati(PapPati p) {
        UserDetails u = new UserDetails();
        u.setEmail(p.getEmail());
        u.setPassword(p.getPassword());
        u.setRole("PAPPATI");
        return u;
    }
}

