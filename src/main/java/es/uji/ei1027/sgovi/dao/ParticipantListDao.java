package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.ParticipantList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class ParticipantListDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void add(ParticipantList participantList) {
        String sql = "INSERT INTO ParticipantList (attendance, attendanceCertificateUrl, activity_id, oviuser_id, pappati_id) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, participantList.isAttendance(), participantList.getAttendanceCertificateUrl(),
                participantList.getIdActivity(), participantList.getIdOviUser(), participantList.getIdPapPati());
    }

    public void update(ParticipantList participantList) {
        String sql = "UPDATE ParticipantList SET attendance=?, attendanceCertificateUrl=?, activity_id=?, oviuser_id=?, pappati_id=? WHERE participantList_id=?";
        jdbcTemplate.update(sql, participantList.isAttendance(), participantList.getAttendanceCertificateUrl(),
                participantList.getIdActivity(), participantList.getIdOviUser(), participantList.getIdPapPati(), participantList.getIdParticipantList());
    }

    public void delete(int idParticipantList) {
        String sql = "DELETE FROM ParticipantList WHERE participantList_id=?";
        jdbcTemplate.update(sql, idParticipantList);
    }

    public ParticipantList get(int idParticipantList) {
        String sql = "SELECT * FROM ParticipantList WHERE participantList_id=?";
        return jdbcTemplate.queryForObject(sql, new ParticipantListRowMapper(), idParticipantList);
    }

    public List<ParticipantList> getAll() {
        String sql = "SELECT * FROM ParticipantList";
        return jdbcTemplate.query(sql, new ParticipantListRowMapper());
    }

    public List<ParticipantList> getByActivity(int idActivity) {
        String sql = "SELECT * FROM ParticipantList WHERE activity_id=?";
        return jdbcTemplate.query(sql, new ParticipantListRowMapper(), idActivity);
    }

    public boolean existsByActivityAndOviUser(int idActivity, int idOviUser) {
        String sql = "SELECT COUNT(*) FROM ParticipantList WHERE activity_id=? AND oviuser_id=?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, idActivity, idOviUser);
        return count != null && count > 0;
    }

    public boolean existsByActivityAndPapPati(int idActivity, int idPapPati) {
        String sql = "SELECT COUNT(*) FROM ParticipantList WHERE activity_id=? AND pappati_id=?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, idActivity, idPapPati);
        return count != null && count > 0;
    }

    public int deleteByActivityAndOviUser(int idActivity, int idOviUser) {
        String sql = "DELETE FROM ParticipantList WHERE activity_id=? AND oviuser_id=?";
        return jdbcTemplate.update(sql, idActivity, idOviUser);
    }

    public int deleteByActivityAndPapPati(int idActivity, int idPapPati) {
        String sql = "DELETE FROM ParticipantList WHERE activity_id=? AND pappati_id=?";
        return jdbcTemplate.update(sql, idActivity, idPapPati);
    }
}

