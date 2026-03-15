package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.OviUser;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OviUserRowMapper implements RowMapper<OviUser> {
    @Override
    public OviUser mapRow(ResultSet rs, int rowNum) throws SQLException {
        OviUser ovilUser = new OviUser();
        ovilUser.setOviuser_id(rs.getInt("oviuser_id"));
        ovilUser.setName(rs.getString("name"));
        ovilUser.setPhone(rs.getString("phone"));
        ovilUser.setPassword(rs.getString("password"));
        ovilUser.setProvince(rs.getString("province"));
        ovilUser.setTown(rs.getString("town"));
        ovilUser.setPc(rs.getString("pc"));
        ovilUser.setAge(rs.getInt("age"));
        ovilUser.setGender(rs.getString("gender"));
        return ovilUser;
    }
}

