package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.Negotiation;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NegotiationRowMapper implements RowMapper<Negotiation> {
    @Override
    public Negotiation mapRow(ResultSet rs, int rowNum) throws SQLException {
        Negotiation negotiation = new Negotiation();
        negotiation.setIdNegotiation(rs.getInt("negotiation_id"));
        negotiation.setStateOfApproval(rs.getString("stateOfApproval"));
        negotiation.setIdRequest(rs.getInt("request_id"));
        negotiation.setIdPapPati(rs.getInt("pappati_id"));
        return negotiation;
    }
}

