package es.uji.ei1027.sgovi.model;

import java.time.LocalDateTime;

public class Message {
    private int idMessage;
    private LocalDateTime messageDateTime;
    private String sender;
    private String receiver;
    private Integer idNegotiation;

    public Message() {}

    public Message(String messageId, LocalDateTime messageDateTime, String sender, String receiver) {
        this.messageDateTime = messageDateTime;
        this.sender = sender;
        this.receiver = receiver;
    }

    public int getIdMessage() { return idMessage; }
    public void setIdMessage(int idMessage) { this.idMessage = idMessage; }
    public LocalDateTime getMessageDateTime() { return messageDateTime; }
    public void setMessageDateTime(LocalDateTime messageDateTime) { this.messageDateTime = messageDateTime; }
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }
    public String getReceiver() { return receiver; }
    public void setReceiver(String receiver) { this.receiver = receiver; }
    public Integer getIdNegotiation() { return idNegotiation; }
    public void setIdNegotiation(Integer idNegotiation) { this.idNegotiation = idNegotiation; }
}

