package es.uji.ei1027.sgovi.model;

public class ParticipantList {
    private int idParticipantList;
    private boolean attendance;
    private String attendanceCertificateUrl;
    private Integer idActivity;
    private Integer idOviUser;
    private Integer idPapPati;

    public ParticipantList() {}

    public ParticipantList(boolean attendance, String attendanceCertificateUrl) {
        this.attendance = attendance;
        this.attendanceCertificateUrl = attendanceCertificateUrl;
    }

    public int getIdParticipantList() { return idParticipantList; }
    public void setIdParticipantList(int idParticipantList) { this.idParticipantList = idParticipantList; }
    public boolean isAttendance() { return attendance; }
    public void setAttendance(boolean attendance) { this.attendance = attendance; }
    public String getAttendanceCertificateUrl() { return attendanceCertificateUrl; }
    public void setAttendanceCertificateUrl(String attendanceCertificateUrl) { this.attendanceCertificateUrl = attendanceCertificateUrl; }

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

