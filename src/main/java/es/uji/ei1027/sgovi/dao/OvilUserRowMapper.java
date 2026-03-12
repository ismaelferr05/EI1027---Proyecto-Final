package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.OvilUser;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OvilUserRowMapper implements RowMapper<OvilUser> {
    @Override
    public OvilUser mapRow(ResultSet rs, int rowNum) throws SQLException {
        OvilUser ovilUser = new OvilUser();
        ovilUser.setIdOvilUser(rs.getInt("id_ovil_user"));
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

