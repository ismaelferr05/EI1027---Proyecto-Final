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
        String sql = "INSERT INTO pap_pati (phone, password, province, town, pc, age, gender, cv_url, training, experience, experience_type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, papPati.getPhone(), papPati.getPassword(), papPati.getProvince(),
                papPati.getTown(), papPati.getPc(), papPati.getAge(), papPati.getGender(), papPati.getCvUrl(),
                papPati.getTraining(), papPati.getExperience(), papPati.getExperienceType());
    }

    public void update(PapPati papPati) {
        String sql = "UPDATE pap_pati SET phone=?, password=?, province=?, town=?, pc=?, age=?, gender=?, cv_url=?, training=?, experience=?, experience_type=? WHERE id_pap_pati=?";
        jdbcTemplate.update(sql, papPati.getPhone(), papPati.getPassword(), papPati.getProvince(),
                papPati.getTown(), papPati.getPc(), papPati.getAge(), papPati.getGender(), papPati.getCvUrl(),
                papPati.getTraining(), papPati.getExperience(), papPati.getExperienceType(), papPati.getPapPati_id());
    }

    public void delete(int idPapPati) {
        String sql = "DELETE FROM pap_pati WHERE id_pap_pati=?";
        jdbcTemplate.update(sql, idPapPati);
    }

    public PapPati get(int idPapPati) {
        String sql = "SELECT * FROM pap_pati WHERE id_pap_pati=?";
        return jdbcTemplate.queryForObject(sql, new PapPatiRowMapper(), idPapPati);
    }

    public List<PapPati> getAll() {
        String sql = "SELECT * FROM pap_pati";
        return jdbcTemplate.query(sql, new PapPatiRowMapper());
    }
}

