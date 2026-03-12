package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.Negotiation;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NegotiationRowMapper implements RowMapper<Negotiation> {
    @Override
    public Negotiation mapRow(ResultSet rs, int rowNum) throws SQLException {
        Negotiation negotiation = new Negotiation();
        negotiation.setIdNegotiation(rs.getInt("id_negotiation"));
        negotiation.setStateOfApproval(rs.getString("state_of_approval"));
        negotiation.setIdRequest(rs.getInt("id_request"));
        negotiation.setIdPapPati(rs.getInt("id_pap_pati"));
        negotiation.setIdContract(rs.getInt("id_contract"));
        return negotiation;
    }
}

