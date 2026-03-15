package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.OviUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class OviUserDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void add(OviUser oviUser) {
        String sql = "INSERT INTO ovi_user (name, phone, password, province, town, pc, age, gender) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, oviUser.getName(), oviUser.getPhone(), oviUser.getPassword(),
                oviUser.getProvince(), oviUser.getTown(), oviUser.getPc(), oviUser.getAge(), oviUser.getGender());
    }

    public void update(OviUser oviUser) {
        String sql = "UPDATE ovi_user SET name=?, phone=?, password=?, province=?, town=?, pc=?, age=?, gender=? WHERE id_ovi_user=?";
        jdbcTemplate.update(sql, oviUser.getName(), oviUser.getPhone(), oviUser.getPassword(),
                oviUser.getProvince(), oviUser.getTown(), oviUser.getPc(), oviUser.getAge(), oviUser.getGender(), oviUser.getOviuser_id());
    }

    public void delete(int idOviUser) {
        String sql = "DELETE FROM ovi_user WHERE id_ovi_user=?";
        jdbcTemplate.update(sql, idOviUser);
    }

    public OviUser get(int oviUser_id) {
        String sql = "SELECT * FROM ovi_user WHERE id_ovi_user=?";
        return jdbcTemplate.queryForObject(sql, new OviUserRowMapper(), oviUser_id);
    }

    public List<OviUser> getAll() {
        String sql = "SELECT * FROM ovi_user";
        return jdbcTemplate.query(sql, new OviUserRowMapper());
    }
}

