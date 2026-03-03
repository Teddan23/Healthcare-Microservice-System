package com.example.FullstackMessageService.Model.Models;

import com.example.FullstackMessageService.Controller.DTOs.MessageDTO;
import jakarta.persistence.*;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDateTime;

@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private Long id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "sender_id", referencedColumnName = "personnummer", nullable = false)
    private User sender;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "receiver_id", referencedColumnName = "personnummer", nullable = false)
    private User receiver;

    @NotNull
    private String message;

    @NotNull
    private LocalDateTime timeStamp;

    @PrePersist
    public void prePersist() {
        if (timeStamp == null) {
            timeStamp = LocalDateTime.now();
        }
    }

    // Getters och setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
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

    public MessageDTO toDTO() {
        return new MessageDTO(
                this.sender.toDTO(),
                this.receiver.toDTO(),
                this.message,
                this.timeStamp
        );
    }

    public static Message fromDTO(MessageDTO dto) {
        Message message = new Message();
        message.sender = User.fromDTO(dto.getSender());
        message.receiver = User.fromDTO(dto.getReceiver());
        message.message = dto.getMessage();
        message.timeStamp = dto.getTimeStamp();
        return message;
    }


}
