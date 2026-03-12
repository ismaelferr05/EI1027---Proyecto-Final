package es.uji.ei1027.sgovi.model;

public class ParticipantList {
    private int idParticipantList;
    private String attendance;
    private String attendanceUrl;
    private Integer idActivity;
    private Integer idOvilUser;
    private Integer idPapPati;

    public ParticipantList() {}

    public ParticipantList(String attendance, String attendanceUrl) {
        this.attendance = attendance;
        this.attendanceUrl = attendanceUrl;
    }

    public int getIdParticipantList() { return idParticipantList; }
    public void setIdParticipantList(int idParticipantList) { this.idParticipantList = idParticipantList; }
    public String getAttendance() { return attendance; }
    public void setAttendance(String attendance) { this.attendance = attendance; }
    public String getAttendanceUrl() { return attendanceUrl; }
    public void setAttendanceUrl(String attendanceUrl) { this.attendanceUrl = attendanceUrl; }
    public Integer getIdActivity() { return idActivity; }
    public void setIdActivity(Integer idActivity) { this.idActivity = idActivity; }
    public Integer getIdOvilUser() { return idOvilUser; }
    public void setIdOvilUser(Integer idOvilUser) { this.idOvilUser = idOvilUser; }
    public Integer getIdPapPati() { return idPapPati; }
    public void setIdPapPati(Integer idPapPati) { this.idPapPati = idPapPati; }
}

