package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.TechnicianCommunication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class TechnicianCommunicationDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void add(TechnicianCommunication communication) {
        String sql = """
                INSERT INTO TechnicianCommunication
                (communicationDateTime, senderRole, senderId, recipientType, recipientId, subject, text)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        jdbcTemplate.update(sql,
                Timestamp.valueOf(communication.getCommunicationDateTime()),
                communication.getSenderRole(),
                communication.getSenderId(),
                communication.getRecipientType(),
                communication.getRecipientId(),
                communication.getSubject(),
                communication.getText());
    }

    public List<TechnicianCommunication> getAll() {
        String sql = "SELECT * FROM TechnicianCommunication ORDER BY communicationDateTime DESC, communication_id DESC";
        return jdbcTemplate.query(sql, new TechnicianCommunicationRowMapper());
    }

    public List<TechnicianCommunication> getByRecipient(String recipientType, int recipientId) {
        String sql = """
                SELECT * FROM TechnicianCommunication
                WHERE recipientType=? AND recipientId=?
                ORDER BY communicationDateTime DESC, communication_id DESC
                """;
        return jdbcTemplate.query(sql, new TechnicianCommunicationRowMapper(), recipientType, recipientId);
    }

    public List<TechnicianCommunication> getConversationForOviUser(int idOviUser) {
        String sql = """
                SELECT * FROM TechnicianCommunication
                WHERE (recipientType='OVIUSER' AND recipientId=?)
                   OR (senderRole='OVIUSER' AND senderId=?)
                ORDER BY communicationDateTime DESC, communication_id DESC
                """;
        return jdbcTemplate.query(sql, new TechnicianCommunicationRowMapper(), idOviUser, idOviUser);
    }

    public List<TechnicianCommunication> getConversationForPapPati(int idPapPati) {
        String sql = """
                SELECT * FROM TechnicianCommunication
                WHERE (recipientType='PAPPATI' AND recipientId=?)
                   OR (senderRole='PAPPATI' AND senderId=?)
                ORDER BY communicationDateTime DESC, communication_id DESC
                """;
        return jdbcTemplate.query(sql, new TechnicianCommunicationRowMapper(), idPapPati, idPapPati);
    }
}
