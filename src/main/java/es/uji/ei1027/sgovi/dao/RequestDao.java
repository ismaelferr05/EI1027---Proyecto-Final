package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.sql.Date;
import java.util.List;

@Repository
public class RequestDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void add(Request request) {
        String sql = "INSERT INTO Request (description, training, startDate, endDate, experience, experienceType, oviuser_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, request.getDescription(), request.getTraining(), Date.valueOf(request.getStartDate()),
                Date.valueOf(request.getEndDate()), request.getExperience(), request.getExperienceType(), request.getIdOvilUser());
    }

    public void update(Request request) {
        String sql = "UPDATE Request SET description=?, training=?, startDate=?, endDate=?, experience=?, experienceType=?, oviuser_id=? WHERE request_id=?";
        jdbcTemplate.update(sql, request.getDescription(), request.getTraining(), Date.valueOf(request.getStartDate()),
                Date.valueOf(request.getEndDate()), request.getExperience(), request.getExperienceType(), request.getIdOvilUser(), request.getIdRequest());
    }

    public void delete(int idRequest) {
        String sql = "DELETE FROM Request WHERE request_id=?";
        jdbcTemplate.update(sql, idRequest);
    }

    public Request get(int idRequest) {
        String sql = "SELECT * FROM Request WHERE request_id=?";
        return jdbcTemplate.queryForObject(sql, new RequestRowMapper(), idRequest);
    }

    public List<Request> getAll() {
        String sql = "SELECT * FROM Request";
        return jdbcTemplate.query(sql, new RequestRowMapper());
    }

    public List<Request> getByOvilUser(int idOvilUser) {
        String sql = "SELECT * FROM Request WHERE oviuser_id=?";
        return jdbcTemplate.query(sql, new RequestRowMapper(), idOvilUser);
    }
}

