package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.Request;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RequestRowMapper implements RowMapper<Request> {
    @Override
    public Request mapRow(ResultSet rs, int rowNum) throws SQLException {
        Request request = new Request();
        request.setIdRequest(rs.getInt("id_request"));
        request.setDescription(rs.getString("description"));
        request.setTraining(rs.getString("training"));
        request.setStartDate(rs.getDate("start_date").toLocalDate());
        request.setEndDate(rs.getDate("end_date").toLocalDate());
        request.setExperience(rs.getString("experience"));
        request.setExperienceType(rs.getString("experience_type"));
        request.setIdOvilUser(rs.getInt("id_ovil_user"));
        return request;
    }
}

