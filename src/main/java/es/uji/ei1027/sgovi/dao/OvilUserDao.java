package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.OvilUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class OvilUserDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void add(OvilUser ovilUser) {
        String sql = "INSERT INTO ovil_user (name, phone, password, province, town, pc, age, gender) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, ovilUser.getName(), ovilUser.getPhone(), ovilUser.getPassword(),
                ovilUser.getProvince(), ovilUser.getTown(), ovilUser.getPc(), ovilUser.getAge(), ovilUser.getGender());
    }

    public void update(OvilUser ovilUser) {
        String sql = "UPDATE ovil_user SET name=?, phone=?, password=?, province=?, town=?, pc=?, age=?, gender=? WHERE id_ovil_user=?";
        jdbcTemplate.update(sql, ovilUser.getName(), ovilUser.getPhone(), ovilUser.getPassword(),
                ovilUser.getProvince(), ovilUser.getTown(), ovilUser.getPc(), ovilUser.getAge(), ovilUser.getGender(), ovilUser.getIdOvilUser());
    }

    public void delete(int idOvilUser) {
        String sql = "DELETE FROM ovil_user WHERE id_ovil_user=?";
        jdbcTemplate.update(sql, idOvilUser);
    }

    public OvilUser get(int idOvilUser) {
        String sql = "SELECT * FROM ovil_user WHERE id_ovil_user=?";
        return jdbcTemplate.queryForObject(sql, new OvilUserRowMapper(), idOvilUser);
    }

    public List<OvilUser> getAll() {
        String sql = "SELECT * FROM ovil_user";
        return jdbcTemplate.query(sql, new OvilUserRowMapper());
    }
}

