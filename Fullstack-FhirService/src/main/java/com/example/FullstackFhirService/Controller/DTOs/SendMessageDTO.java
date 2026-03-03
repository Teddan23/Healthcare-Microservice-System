package com.example.FullstackFhirService.Controller.DTOs;

import java.time.LocalDateTime;

public class SendMessageDTO {
    private String senderPersonnummer;

    private String receiverPersonnummer;

    private String message;

    private LocalDateTime timeStamp;

    public SendMessageDTO(String senderPersonnummer, String receiverPersonnummer, String message, LocalDateTime timeStamp) {
        this.senderPersonnummer = senderPersonnummer;
        this.receiverPersonnummer = receiverPersonnummer;
        this.message = message;
        this.timeStamp = timeStamp;
    }

    public String getSenderPersonnummer() {
        return senderPersonnummer;
    }

    public void setSenderPersonnummer(String senderPersonnummer) {
        this.senderPersonnummer = senderPersonnummer;
    }

    public String getReceiverPersonnummer() {
        return receiverPersonnummer;
    }

    public void setReceiverPersonnummer(String receiverPersonnummer) {
        this.receiverPersonnummer = receiverPersonnummer;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }


}
