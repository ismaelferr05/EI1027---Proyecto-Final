package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.TechnicianCommunication;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TechnicianCommunicationRowMapper implements RowMapper<TechnicianCommunication> {
    @Override
    public TechnicianCommunication mapRow(ResultSet rs, int rowNum) throws SQLException {
        TechnicianCommunication communication = new TechnicianCommunication();
        communication.setIdCommunication(rs.getInt("communication_id"));
        communication.setCommunicationDateTime(rs.getTimestamp("communicationDateTime").toLocalDateTime());
        communication.setSenderRole(rs.getString("senderRole"));
        communication.setRecipientType(rs.getString("recipientType"));
        communication.setRecipientId(rs.getInt("recipientId"));
        communication.setSubject(rs.getString("subject"));
        communication.setText(rs.getString("text"));
        return communication;
    }
}
