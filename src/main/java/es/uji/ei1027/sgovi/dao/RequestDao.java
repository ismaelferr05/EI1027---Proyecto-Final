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

    public int add(Request request) {
        String sql = "INSERT INTO Request (description, training, startDate, endDate, experience, experienceType, preferredGender, preferredPc, preferredAge, status, rejectionReason, oviuser_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING request_id";
        int generatedId = jdbcTemplate.queryForObject(sql, Integer.class,
                request.getDescription(), request.getTraining(), Date.valueOf(request.getStartDate()),
                Date.valueOf(request.getEndDate()), request.getExperience(), request.getExperienceType(), request.getPreferredGender(),
                request.getPreferredPc(), request.getPreferredAge(), request.getStatus(), request.getRejectionReason(), request.getIdOviUser());
        request.setIdRequest(generatedId);
        return generatedId;
    }

    public void update(Request request) {
        String sql = "UPDATE Request SET description=?, training=?, startDate=?, endDate=?, experience=?, experienceType=?, preferredGender=?, preferredPc=?, preferredAge=?, status=?, rejectionReason=?, oviuser_id=? WHERE request_id=?";
        jdbcTemplate.update(sql, request.getDescription(), request.getTraining(), Date.valueOf(request.getStartDate()),
                Date.valueOf(request.getEndDate()), request.getExperience(), request.getExperienceType(), request.getPreferredGender(),
                request.getPreferredPc(), request.getPreferredAge(), request.getStatus(), request.getRejectionReason(), request.getIdOviUser(), request.getIdRequest());
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

    public List<Request> getByOviUser(int idOviUser) {
        String sql = "SELECT * FROM Request WHERE oviuser_id=?";
        return jdbcTemplate.query(sql, new RequestRowMapper(), idOviUser);
    }

    public List<Request> getByStatus(String status) {
        String sql = "SELECT * FROM Request WHERE status=? ORDER BY startDate";
        return jdbcTemplate.query(sql, new RequestRowMapper(), status);
    }

    public void updateStatus(int idRequest, String status) {
        updateStatus(idRequest, status, null);
    }

    public void updateStatus(int idRequest, String status, String rejectionReason) {
        String sql = "UPDATE Request SET status=?, rejectionReason=? WHERE request_id=?";
        jdbcTemplate.update(sql, status, rejectionReason, idRequest);
    }
}
