package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.PapPati;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class PapPatiDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void add(PapPati papPati) {
        String sql = "INSERT INTO PapPati (name, lastName, email, phone, password, province, town, pc, age, gender, cvUrl, training, experience, experienceType, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, papPati.getName(), papPati.getLastName(), papPati.getEmail(), papPati.getPhone(), papPati.getPassword(), papPati.getProvince(),
                papPati.getTown(), papPati.getPc(), papPati.getAge(), papPati.getGender(), papPati.getCvUrl(), papPati.getTraining(),
                papPati.getExperience(), papPati.getExperienceType(), papPati.getStatus());
    }

    public void update(PapPati papPati) {
        String sql = "UPDATE PapPati SET name=?, lastName=?, email=?, phone=?, password=?, province=?, town=?, pc=?, age=?, gender=?, cvUrl=?, training=?, experience=?, experienceType=?, status=? WHERE pappati_id=?";
        jdbcTemplate.update(sql, papPati.getName(), papPati.getLastName(), papPati.getEmail(), papPati.getPhone(), papPati.getPassword(), papPati.getProvince(),
                papPati.getTown(), papPati.getPc(), papPati.getAge(), papPati.getGender(), papPati.getCvUrl(), papPati.getTraining(),
                papPati.getExperience(), papPati.getExperienceType(), papPati.getStatus(), papPati.getIdPapPati());
    }

    public void delete(int idPapPati) {
        String sql = "DELETE FROM PapPati WHERE pappati_id=?";
        jdbcTemplate.update(sql, idPapPati);
    }

    public PapPati get(int idPapPati) {
        String sql = "SELECT * FROM PapPati WHERE pappati_id=?";
        return jdbcTemplate.queryForObject(sql, new PapPatiRowMapper(), idPapPati);
    }

    public List<PapPati> getAll() {
        String sql = "SELECT * FROM PapPati";
        return jdbcTemplate.query(sql, new PapPatiRowMapper());
    }

    public List<PapPati> getByStatus(String status) {
        String sql = "SELECT * FROM PapPati WHERE status=?";
        return jdbcTemplate.query(sql, new PapPatiRowMapper(), status);
    }

    public PapPati getByEmail(String email) {
        String sql = "SELECT * FROM PapPati WHERE LOWER(email)=LOWER(?)";
        List<PapPati> users = jdbcTemplate.query(sql, new PapPatiRowMapper(), email);
        return users.isEmpty() ? null : users.get(0);
    }
}

