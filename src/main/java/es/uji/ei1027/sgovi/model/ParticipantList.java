package es.uji.ei1027.sgovi.model;

public class ParticipantList {
    private int idParticipantList;
    private boolean attendance;
    private String attendanceUrl;
    private Integer idActivity;
    private Integer idOviUser;
    private Integer idPapPati;

    public ParticipantList() {}

    public ParticipantList(boolean attendance, String attendanceUrl) {
        this.attendance = attendance;
        this.attendanceUrl = attendanceUrl;
    }

    public int getIdParticipantList() { return idParticipantList; }
    public void setIdParticipantList(int idParticipantList) { this.idParticipantList = idParticipantList; }
    public boolean getAttendance() { return attendance; }
    public void setAttendance(boolean attendance) { this.attendance = attendance; }
    public String getAttendanceUrl() { return attendanceUrl; }
    public void setAttendanceUrl(String attendanceUrl) { this.attendanceUrl = attendanceUrl; }

    public Integer getIdActivity() {
        return idActivity;
    }

    public void setIdActivity(Integer idActivity) {
        this.idActivity = idActivity;
    }

    public Integer getIdOviUser() {
        return idOviUser;
    }

    public void setIdOviUser(Integer idOviUser) {
        this.idOviUser = idOviUser;
    }

    public Integer getIdPapPati() {
        return idPapPati;
    }

    public void setIdPapPati(Integer idPapPati) {
        this.idPapPati = idPapPati;
    }
}

