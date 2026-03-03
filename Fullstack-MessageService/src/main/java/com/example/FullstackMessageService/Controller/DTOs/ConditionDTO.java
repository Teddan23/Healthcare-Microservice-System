package com.example.FullstackMessageService.Controller.DTOs;

import java.util.Date;

public class ConditionDTO {
    private String id;
    private String code; // Kod för diagnosen, t.ex. ICD-10 kod
    private String description; // Beskrivning av diagnosen
    private Date onsetDate; // Datum när symtom började

    public ConditionDTO(String id, String code, String description, Date onsetDate) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.onsetDate = onsetDate;
    }

    public ConditionDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getOnsetDate() {
        return onsetDate;
    }

    public void setOnsetDate(Date onsetDate) {
        this.onsetDate = onsetDate;
    }
}