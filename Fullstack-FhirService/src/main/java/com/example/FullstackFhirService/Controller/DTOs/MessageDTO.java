package com.example.FullstackFhirService.Controller.DTOs;

import java.time.LocalDateTime;

public class MessageDTO {

    private UserDTO sender;

    private UserDTO receiver;

    private String message;

    private LocalDateTime timeStamp;

    public MessageDTO( UserDTO sender, UserDTO receiver, String message, LocalDateTime timeStamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.timeStamp = timeStamp;
    }


    public UserDTO getSender() {
        return sender;
    }

    public void setSender(UserDTO sender) {
        this.sender = sender;
    }

    public UserDTO getReceiver() {
        return receiver;
    }

    public void setReceiver(UserDTO receiver) {
        this.receiver = receiver;
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
