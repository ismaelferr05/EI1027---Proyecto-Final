package es.uji.ei1027.sgovi.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EmailContent {
    private String to;
    private String from;
    private String subject;
    private String body;
    private LocalDateTime sentAt;

    public EmailContent() {
    }

    public EmailContent(String to, String from, String subject, String body, LocalDateTime sentAt) {
        this.to = to;
        this.from = from;
        this.subject = subject;
        this.body = body;
        this.sentAt = sentAt;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public String getFormattedDate() {
        return sentAt == null ? "" : sentAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}

