package com.example.FullstackUserService.Controller.DTOs;

import java.util.Date;

public class CommunicationDTO {
    private String id;
    private String subject; // Exempelvis ämnet för meddelandet
    private String status;  // Statusen för meddelandet (ex. "sent", "received")
    private Date dateSent;  // Datum då meddelandet skickades

    public CommunicationDTO(String id, String subject, String status, Date dateSent) {
        this.id = id;
        this.subject = subject;
        this.status = status;
        this.dateSent = dateSent;
    }

    public CommunicationDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDateSent() {
        return dateSent;
    }

    public void setDateSent(Date dateSent) {
        this.dateSent = dateSent;
    }
}