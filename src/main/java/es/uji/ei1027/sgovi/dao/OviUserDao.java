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
        String sql = "INSERT INTO OviUser (name, phone, password, province, town, pc, age, gender) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, oviUser.getName(), oviUser.getPhone(), oviUser.getPassword(),
                oviUser.getProvince(), oviUser.getTown(), oviUser.getPc(), oviUser.getAge(), oviUser.getGender());
    }

    public void update(OviUser oviUser) {
        String sql = "UPDATE OviUser SET name=?, phone=?, password=?, province=?, town=?, pc=?, age=?, gender=? WHERE oviuser_id=?";
        jdbcTemplate.update(sql, oviUser.getName(), oviUser.getPhone(), oviUser.getPassword(),
                oviUser.getProvince(), oviUser.getTown(), oviUser.getPc(), oviUser.getAge(), oviUser.getGender(), oviUser.getOviuser_id());
    }

    public void delete(int idOviUser) {
        String sql = "DELETE FROM OviUser WHERE oviuser_id=?";
        jdbcTemplate.update(sql, idOviUser);
    }

    public OviUser get(int oviUser_id) {
        String sql = "SELECT * FROM OviUser WHERE oviuser_id=?";
        return jdbcTemplate.queryForObject(sql, new OviUserRowMapper(), oviUser_id);
    }

    public List<OviUser> getAll() {
        String sql = "SELECT * FROM OviUser";
        return jdbcTemplate.query(sql, new OviUserRowMapper());
    }
}

