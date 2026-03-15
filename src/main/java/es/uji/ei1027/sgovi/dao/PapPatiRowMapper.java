package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.PapPati;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PapPatiRowMapper implements RowMapper<PapPati> {
    @Override
    public PapPati mapRow(ResultSet rs, int rowNum) throws SQLException {
        PapPati papPati = new PapPati();
        papPati.setPapPati_id(rs.getInt("pappati_id"));
        papPati.setPhone(rs.getString("phone"));
        papPati.setPassword(rs.getString("password"));
        papPati.setProvince(rs.getString("province"));
        papPati.setTown(rs.getString("town"));
        papPati.setPc(rs.getString("pc"));
        papPati.setAge(rs.getInt("age"));
        papPati.setGender(rs.getString("gender"));
        papPati.setCvUrl(rs.getString("cvUrl"));
        papPati.setTraining(rs.getString("training"));
        papPati.setExperience(rs.getString("experience"));
        papPati.setExperienceType(rs.getString("experienceType"));
        return papPati;
    }
}

