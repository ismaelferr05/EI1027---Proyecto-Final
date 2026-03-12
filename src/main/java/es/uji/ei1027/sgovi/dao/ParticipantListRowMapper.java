package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.ParticipantList;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ParticipantListRowMapper implements RowMapper<ParticipantList> {
    @Override
    public ParticipantList mapRow(ResultSet rs, int rowNum) throws SQLException {
        ParticipantList participantList = new ParticipantList();
        participantList.setIdParticipantList(rs.getInt("id_participant_list"));
        participantList.setAttendance(rs.getString("attendance"));
        participantList.setAttendanceUrl(rs.getString("attendance_url"));
        participantList.setIdActivity(rs.getInt("id_activity"));
        participantList.setIdOvilUser(rs.getInt("id_ovil_user"));
        participantList.setIdPapPati(rs.getInt("id_pap_pati"));
        return participantList;
    }
}

