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
        String sql = "INSERT INTO participant_list (attendance, attendance_url, id_activity, id_ovil_user, id_pap_pati) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, participantList.getAttendance(), participantList.getAttendanceUrl(),
                participantList.getActivity_id(), participantList.getOviuser_id(), participantList.getPapPati_id());
    }

    public void update(ParticipantList participantList) {
        String sql = "UPDATE participant_list SET attendance=?, attendance_url=?, id_activity=?, id_ovil_user=?, id_pap_pati=? WHERE id_participant_list=?";
        jdbcTemplate.update(sql, participantList.getAttendance(), participantList.getAttendanceUrl(),
                participantList.getActivity_id(), participantList.getOviuser_id(), participantList.getPapPati_id(), participantList.getIdParticipantList());
    }

    public void delete(int idParticipantList) {
        String sql = "DELETE FROM participant_list WHERE id_participant_list=?";
        jdbcTemplate.update(sql, idParticipantList);
    }

    public ParticipantList get(int idParticipantList) {
        String sql = "SELECT * FROM participant_list WHERE id_participant_list=?";
        return jdbcTemplate.queryForObject(sql, new ParticipantListRowMapper(), idParticipantList);
    }

    public List<ParticipantList> getAll() {
        String sql = "SELECT * FROM participant_list";
        return jdbcTemplate.query(sql, new ParticipantListRowMapper());
    }

    public List<ParticipantList> getByActivity(int idActivity) {
        String sql = "SELECT * FROM participant_list WHERE id_activity=?";
        return jdbcTemplate.query(sql, new ParticipantListRowMapper(), idActivity);
    }
}

