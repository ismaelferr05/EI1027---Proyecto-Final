package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class MessageDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void add(Message message) {
        String sql = "INSERT INTO message (message_id, message_date_time, sender, receiver, content, id_negotiation) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, message.getMessageId(), Timestamp.valueOf(message.getMessageDateTime()),
                message.getSender(), message.getReceiver(), message.getContent(), message.getIdNegotiation());
    }

    public void update(Message message) {
        String sql = "UPDATE message SET message_id=?, message_date_time=?, sender=?, receiver=?, content=?, id_negotiation=? WHERE id_message=?";
        jdbcTemplate.update(sql, message.getMessageId(), Timestamp.valueOf(message.getMessageDateTime()),
                message.getSender(), message.getReceiver(), message.getContent(), message.getIdNegotiation(), message.getIdMessage());
    }

    public void delete(int idMessage) {
        String sql = "DELETE FROM message WHERE id_message=?";
        jdbcTemplate.update(sql, idMessage);
    }

    public Message get(int idMessage) {
        String sql = "SELECT * FROM message WHERE id_message=?";
        return jdbcTemplate.queryForObject(sql, new MessageRowMapper(), idMessage);
    }

    public List<Message> getAll() {
        String sql = "SELECT * FROM message";
        return jdbcTemplate.query(sql, new MessageRowMapper());
    }

    public List<Message> getByNegotiation(int idNegotiation) {
        String sql = "SELECT * FROM message WHERE id_negotiation=?";
        return jdbcTemplate.query(sql, new MessageRowMapper(), idNegotiation);
    }
}

