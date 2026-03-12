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
        String sql = "INSERT INTO negotiation (state_of_approval, id_request, id_pap_pati, id_contract) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, negotiation.getStateOfApproval(), negotiation.getIdRequest(),
                negotiation.getIdPapPati(), negotiation.getIdContract());
    }

    public void update(Negotiation negotiation) {
        String sql = "UPDATE negotiation SET state_of_approval=?, id_request=?, id_pap_pati=?, id_contract=? WHERE id_negotiation=?";
        jdbcTemplate.update(sql, negotiation.getStateOfApproval(), negotiation.getIdRequest(),
                negotiation.getIdPapPati(), negotiation.getIdContract(), negotiation.getIdNegotiation());
    }

    public void delete(int idNegotiation) {
        String sql = "DELETE FROM negotiation WHERE id_negotiation=?";
        jdbcTemplate.update(sql, idNegotiation);
    }

    public Negotiation get(int idNegotiation) {
        String sql = "SELECT * FROM negotiation WHERE id_negotiation=?";
        return jdbcTemplate.queryForObject(sql, new NegotiationRowMapper(), idNegotiation);
    }

    public List<Negotiation> getAll() {
        String sql = "SELECT * FROM negotiation";
        return jdbcTemplate.query(sql, new NegotiationRowMapper());
    }

    public List<Negotiation> getByRequest(int idRequest) {
        String sql = "SELECT * FROM negotiation WHERE id_request=?";
        return jdbcTemplate.query(sql, new NegotiationRowMapper(), idRequest);
    }

    public List<Negotiation> getByPapPati(int idPapPati) {
        String sql = "SELECT * FROM negotiation WHERE id_pap_pati=?";
        return jdbcTemplate.query(sql, new NegotiationRowMapper(), idPapPati);
    }
}

