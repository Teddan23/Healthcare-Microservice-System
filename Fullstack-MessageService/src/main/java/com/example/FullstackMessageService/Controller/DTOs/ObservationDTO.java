package com.example.FullstackMessageService.Controller.DTOs;

public class ObservationDTO {
    private String id;
    private String code; // Kod för observationen, t.ex. blodtryck, temperatur
    private String value; // Värdet på observationen, t.ex. 120/80, 36.5°C
    private String date; // Datum och tid för observationen

    public ObservationDTO(String id, String code, String value, String date) {
        this.id = id;
        this.code = code;
        this.value = value;
        this.date = date;
    }

    public ObservationDTO() {
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}