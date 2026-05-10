package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

@Repository
public class FakeUserProvider implements UserDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public UserDetails getUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }

        String normalizedEmail = email.trim();

        UserDetails user = querySingle("SELECT email, password FROM OviUser WHERE LOWER(email)=LOWER(?)", normalizedEmail, "OVIUSER");
        if (user != null) {
            return user;
        }

        user = querySingle("SELECT email, password FROM PapPati WHERE LOWER(email)=LOWER(?)", normalizedEmail, "PAPPATI");
        if (user != null) {
            return user;
        }

        return querySingle("SELECT email, password FROM Technician WHERE LOWER(email)=LOWER(?)", normalizedEmail, "TECNICO");
    }

    @Override
    public Collection<UserDetails> getAllUsers() {
        List<UserDetails> users = new ArrayList<>();
        users.addAll(queryUsers("SELECT email, password FROM OviUser", "OVIUSER"));
        users.addAll(queryUsers("SELECT email, password FROM PapPati", "PAPPATI"));
        users.addAll(queryUsers("SELECT email, password FROM Technician", "TECNICO"));
        return users;
    }

    private UserDetails querySingle(String sql, String email, String role) {
        List<UserDetails> users = jdbcTemplate.query(sql, (rs, rowNum) -> {
            UserDetails user = new UserDetails();
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            user.setRole(role);
            return user;
        }, email);
        return users.isEmpty() ? null : users.get(0);
    }

    private Collection<UserDetails> queryUsers(String sql, String role) {
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            UserDetails user = new UserDetails();
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            user.setRole(role);
            return user;
        });
    }
}
