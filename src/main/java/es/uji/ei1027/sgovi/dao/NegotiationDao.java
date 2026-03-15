package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.Negotiation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class NegotiationDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void add(Negotiation negotiation) {
        String sql = "INSERT INTO Negotiation (stateOfApproval, request_id, pappati_id) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, negotiation.getStateOfApproval(), negotiation.getIdRequest(),
                negotiation.getIdPapPati());
    }

    public void update(Negotiation negotiation) {
        String sql = "UPDATE Negotiation SET stateOfApproval=?, request_id=?, pappati_id=? WHERE negotiation_id=?";
        jdbcTemplate.update(sql, negotiation.getStateOfApproval(), negotiation.getIdRequest(),
                negotiation.getIdPapPati(), negotiation.getIdNegotiation());
    }

    public void delete(int idNegotiation) {
        String sql = "DELETE FROM Negotiation WHERE negotiation_id=?";
        jdbcTemplate.update(sql, idNegotiation);
    }

    public Negotiation get(int idNegotiation) {
        String sql = "SELECT * FROM Negotiation WHERE negotiation_id=?";
        return jdbcTemplate.queryForObject(sql, new NegotiationRowMapper(), idNegotiation);
    }

    public List<Negotiation> getAll() {
        String sql = "SELECT * FROM Negotiation";
        return jdbcTemplate.query(sql, new NegotiationRowMapper());
    }

    public List<Negotiation> getByRequest(int idRequest) {
        String sql = "SELECT * FROM Negotiation WHERE request_id=?";
        return jdbcTemplate.query(sql, new NegotiationRowMapper(), idRequest);
    }

    public List<Negotiation> getByPapPati(int idPapPati) {
        String sql = "SELECT * FROM Negotiation WHERE pappati_id=?";
        return jdbcTemplate.query(sql, new NegotiationRowMapper(), idPapPati);
    }
}

