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
        participantList.setAttendance(rs.getString("attendance"));
        participantList.setAttendanceUrl(rs.getString("attendanceUrl"));
        participantList.setActivity_id(rs.getInt("activity_id"));
        participantList.setOviuser_id(rs.getInt("oviuser_id"));
        participantList.setPapPati_id(rs.getInt("pappati_id"));
        return participantList;
    }
}

