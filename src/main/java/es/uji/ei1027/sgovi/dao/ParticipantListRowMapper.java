package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.ParticipantList;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ParticipantListRowMapper implements RowMapper<ParticipantList> {
    @Override
    public ParticipantList mapRow(ResultSet rs, int rowNum) throws SQLException {
        ParticipantList participantList = new ParticipantList();
        participantList.setIdParticipantList(rs.getInt("participantList_id"));
        participantList.setAttendance(rs.getBoolean("attendance"));
        participantList.setAttendanceUrl(rs.getString("attendanceUrl"));
        participantList.setIdActivity(rs.getObject("activity_id", Integer.class));
        participantList.setIdOviUser(rs.getObject("oviuser_id", Integer.class));
        participantList.setIdPapPati(rs.getObject("pappati_id", Integer.class));
        return participantList;
    }
}

