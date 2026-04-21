package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.OviUser;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OviUserRowMapper implements RowMapper<OviUser> {
    @Override
    public OviUser mapRow(ResultSet rs, int rowNum) throws SQLException {
        OviUser oviUser = new OviUser();
        oviUser.setIdOviUser(rs.getInt("oviuser_id"));
        oviUser.setName(rs.getString("name"));
        oviUser.setLastName(rs.getString("lastName"));
        oviUser.setEmail(rs.getString("email"));
        oviUser.setPhone(rs.getString("phone"));
        oviUser.setPassword(rs.getString("password"));
        oviUser.setProvince(rs.getString("province"));
        oviUser.setTown(rs.getString("town"));
        oviUser.setPc(rs.getString("pc"));
        oviUser.setAge((Integer) rs.getObject("age"));
        oviUser.setGender(rs.getString("gender"));
        oviUser.setStatus(rs.getString("status"));
        oviUser.setLopdConsent(rs.getBoolean("lopdConsent"));
        return oviUser;
    }
}

