package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.Message;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MessageRowMapper implements RowMapper<Message> {
    @Override
    public Message mapRow(ResultSet rs, int rowNum) throws SQLException {
        Message message = new Message();
        message.setIdMessage(rs.getInt("messageId"));
        message.setMessageDateTime(rs.getTimestamp("messageDateTime").toLocalDateTime());
        message.setSender(rs.getString("sender"));
        message.setReceiver(rs.getString("receiver"));
        message.setIdNegotiation(rs.getInt("negotiation_id"));
        return message;
    }
}

