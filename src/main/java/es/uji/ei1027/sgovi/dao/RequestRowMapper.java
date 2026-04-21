package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.Request;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RequestRowMapper implements RowMapper<Request> {
    @Override
    public Request mapRow(ResultSet rs, int rowNum) throws SQLException {
        Request request = new Request();
        request.setIdRequest(rs.getInt("request_id"));
        request.setDescription(rs.getString("description"));
        request.setTraining(rs.getString("training"));
        request.setStartDate(rs.getDate("startDate").toLocalDate());
        request.setEndDate(rs.getDate("endDate").toLocalDate());
        Object experience = rs.getObject("experience");
        request.setExperience(experience != null ? rs.getInt("experience") : null);
        request.setExperienceType(rs.getString("experienceType"));
        request.setPreferredGender(rs.getString("preferredGender"));
        request.setPreferredPc(rs.getString("preferredPc"));
        Object preferredAge = rs.getObject("preferredAge");
        request.setPreferredAge(preferredAge != null ? rs.getInt("preferredAge") : null);
        request.setStatus(rs.getString("status"));
        request.setIdOviUser(rs.getInt("oviuser_id"));
        return request;
    }
}

