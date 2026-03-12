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
        String sql = "INSERT INTO request (description, training, start_date, end_date, experience, experience_type, id_ovil_user) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, request.getDescription(), request.getTraining(), Date.valueOf(request.getStartDate()),
                Date.valueOf(request.getEndDate()), request.getExperience(), request.getExperienceType(), request.getIdOvilUser());
    }

    public void update(Request request) {
        String sql = "UPDATE request SET description=?, training=?, start_date=?, end_date=?, experience=?, experience_type=?, id_ovil_user=? WHERE id_request=?";
        jdbcTemplate.update(sql, request.getDescription(), request.getTraining(), Date.valueOf(request.getStartDate()),
                Date.valueOf(request.getEndDate()), request.getExperience(), request.getExperienceType(), request.getIdOvilUser(), request.getIdRequest());
    }

    public void delete(int idRequest) {
        String sql = "DELETE FROM request WHERE id_request=?";
        jdbcTemplate.update(sql, idRequest);
    }

    public Request get(int idRequest) {
        String sql = "SELECT * FROM request WHERE id_request=?";
        return jdbcTemplate.queryForObject(sql, new RequestRowMapper(), idRequest);
    }

    public List<Request> getAll() {
        String sql = "SELECT * FROM request";
        return jdbcTemplate.query(sql, new RequestRowMapper());
    }

    public List<Request> getByOvilUser(int idOvilUser) {
        String sql = "SELECT * FROM request WHERE id_ovil_user=?";
        return jdbcTemplate.query(sql, new RequestRowMapper(), idOvilUser);
    }
}

