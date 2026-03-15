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
        String sql = "INSERT INTO PapPati (phone, password, province, town, pc, age, gender, cvUrl, training, experience, experienceType) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, papPati.getPhone(), papPati.getPassword(), papPati.getProvince(),
                papPati.getTown(), papPati.getPc(), papPati.getAge(), papPati.getGender(), papPati.getCvUrl(),
                papPati.getTraining(), papPati.getExperience(), papPati.getExperienceType());
    }

    public void update(PapPati papPati) {
        String sql = "UPDATE PapPati SET phone=?, password=?, province=?, town=?, pc=?, age=?, gender=?, cvUrl=?, training=?, experience=?, experienceType=? WHERE pappati_id=?";
        jdbcTemplate.update(sql, papPati.getPhone(), papPati.getPassword(), papPati.getProvince(),
                papPati.getTown(), papPati.getPc(), papPati.getAge(), papPati.getGender(), papPati.getCvUrl(),
                papPati.getTraining(), papPati.getExperience(), papPati.getExperienceType(), papPati.getPapPati_id());
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
}

