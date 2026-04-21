package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.PapPati;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PapPatiRowMapper implements RowMapper<PapPati> {
    @Override
    public PapPati mapRow(ResultSet rs, int rowNum) throws SQLException {
        PapPati papPati = new PapPati();
        papPati.setIdPapPati(rs.getInt("pappati_id"));
        papPati.setName(rs.getString("name"));
        papPati.setLastName(rs.getString("lastName"));
        papPati.setEmail(rs.getString("email"));
        papPati.setPhone(rs.getString("phone"));
        papPati.setPassword(rs.getString("password"));
        papPati.setProvince(rs.getString("province"));
        papPati.setTown(rs.getString("town"));
        papPati.setPc(rs.getString("pc"));
        papPati.setAge((Integer) rs.getObject("age"));
        papPati.setGender(rs.getString("gender"));
        papPati.setCvUrl(rs.getString("cvUrl"));
        papPati.setTraining(rs.getString("training"));
        papPati.setExperience(rs.getString("experience"));
        papPati.setExperienceType(rs.getString("experienceType"));
        papPati.setStatus(rs.getString("status"));
        return papPati;
    }
}

