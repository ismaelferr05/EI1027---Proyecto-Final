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
        String sql = "INSERT INTO Message (messageId, messageDateTime, sender, receiver, negotiation_id) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, Timestamp.valueOf(message.getMessageDateTime()),
                message.getSender(), message.getReceiver(), message.getIdNegotiation());
    }

    public void update(Message message) {
        String sql = "UPDATE Message SET messageId=?, messageDateTime=?, sender=?, receiver=?, negotiation_id=? WHERE messageId=?";
        jdbcTemplate.update(sql, Timestamp.valueOf(message.getMessageDateTime()),
                message.getSender(), message.getReceiver(), message.getIdNegotiation(), message.getIdMessage());
    }

    public void delete(int idMessage) {
        String sql = "DELETE FROM Message WHERE messageId=?";
        jdbcTemplate.update(sql, idMessage);
    }

    public Message get(int idMessage) {
        String sql = "SELECT * FROM Message WHERE messageId=?";
        return jdbcTemplate.queryForObject(sql, new MessageRowMapper(), idMessage);
    }

    public List<Message> getAll() {
        String sql = "SELECT * FROM Message";
        return jdbcTemplate.query(sql, new MessageRowMapper());
    }

    public List<Message> getByNegotiation(int idNegotiation) {
        String sql = "SELECT * FROM Message WHERE negotiation_id=?";
        return jdbcTemplate.query(sql, new MessageRowMapper(), idNegotiation);
    }
}

