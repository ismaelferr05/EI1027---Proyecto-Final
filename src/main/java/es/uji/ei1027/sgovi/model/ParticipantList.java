package es.uji.ei1027.sgovi.model;

public class ParticipantList {
    private int participantList_id;
    private String attendance;
    private String attendanceUrl;
    private Integer activity_id;
    private Integer oviuser_id;
    private Integer PapPati_id;

    public ParticipantList() {}

    public ParticipantList(String attendance, String attendanceUrl) {
        this.attendance = attendance;
        this.attendanceUrl = attendanceUrl;
    }

    public int getIdParticipantList() { return participantList_id; }
    public void setIdParticipantList(int idParticipantList) { this.participantList_id = idParticipantList; }
    public String getAttendance() { return attendance; }
    public void setAttendance(String attendance) { this.attendance = attendance; }
    public String getAttendanceUrl() { return attendanceUrl; }
    public void setAttendanceUrl(String attendanceUrl) { this.attendanceUrl = attendanceUrl; }

    public Integer getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(Integer activity_id) {
        this.activity_id = activity_id;
    }

    public Integer getOviuser_id() {
        return oviuser_id;
    }

    public void setOviuser_id(Integer oviuser_id) {
        this.oviuser_id = oviuser_id;
    }

    public Integer getPapPati_id() {
        return PapPati_id;
    }

    public void setPapPati_id(Integer papPati_id) {
        PapPati_id = papPati_id;
    }
}

