package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.Message;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MessageRowMapper implements RowMapper<Message> {
    @Override
    public Message mapRow(ResultSet rs, int rowNum) throws SQLException {
        Message message = new Message();
        message.setIdMessage(rs.getInt("id_message"));
        message.setMessageId(rs.getString("message_id"));
        message.setMessageDateTime(rs.getTimestamp("message_date_time").toLocalDateTime());
        message.setSender(rs.getString("sender"));
        message.setReceiver(rs.getString("receiver"));
        message.setContent(rs.getString("content"));
        message.setIdNegotiation(rs.getInt("id_negotiation"));
        return message;
    }
}

