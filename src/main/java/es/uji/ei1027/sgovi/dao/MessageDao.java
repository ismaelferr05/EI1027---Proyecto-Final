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
        String sql = "INSERT INTO Message (messageDateTime, sender, receiver, text, negotiation_id) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, Timestamp.valueOf(message.getMessageDateTime()),
                message.getSender(), message.getReceiver(), message.getText(), message.getIdNegotiation());
    }

    public void update(Message message) {
        String sql = "UPDATE Message SET messageDateTime=?, sender=?, receiver=?, text=?, negotiation_id=? WHERE messageId=?";
        jdbcTemplate.update(sql, Timestamp.valueOf(message.getMessageDateTime()),
                message.getSender(), message.getReceiver(), message.getText(), message.getIdNegotiation(), message.getIdMessage());
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

    public List<Message> getByOviUser(int idOviUser) {
        String sql = """
                SELECT m.*
                FROM Message m
                JOIN Negotiation n ON m.negotiation_id = n.negotiation_id
                JOIN Request r ON n.request_id = r.request_id
                WHERE r.oviuser_id = ?
                ORDER BY m.messageDateTime
                """;
        return jdbcTemplate.query(sql, new MessageRowMapper(), idOviUser);
    }

    public List<Message> getByPapPati(int idPapPati) {
        String sql = """
                SELECT m.*
                FROM Message m
                JOIN Negotiation n ON m.negotiation_id = n.negotiation_id
                WHERE n.pappati_id = ?
                ORDER BY m.messageDateTime
                """;
        return jdbcTemplate.query(sql, new MessageRowMapper(), idPapPati);
    }
}

