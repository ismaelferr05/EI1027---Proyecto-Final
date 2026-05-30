package es.uji.ei1027.sgovi.model;

import java.time.LocalDateTime;

public class TechnicianCommunication {
    private int idCommunication;
    private LocalDateTime communicationDateTime;
    private String senderRole;
    private String recipientType;
    private Integer recipientId;
    private String subject;
    private String text;

    public int getIdCommunication() { return idCommunication; }
    public void setIdCommunication(int idCommunication) { this.idCommunication = idCommunication; }
    public LocalDateTime getCommunicationDateTime() { return communicationDateTime; }
    public void setCommunicationDateTime(LocalDateTime communicationDateTime) { this.communicationDateTime = communicationDateTime; }
    public String getSenderRole() { return senderRole; }
    public void setSenderRole(String senderRole) { this.senderRole = senderRole; }
    public String getRecipientType() { return recipientType; }
    public void setRecipientType(String recipientType) { this.recipientType = recipientType; }
    public Integer getRecipientId() { return recipientId; }
    public void setRecipientId(Integer recipientId) { this.recipientId = recipientId; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}
