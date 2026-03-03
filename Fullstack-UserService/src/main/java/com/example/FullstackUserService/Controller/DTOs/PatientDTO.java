package com.example.FullstackUserService.Controller.DTOs;

import java.util.Date;
import java.util.List;

public class PatientDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String gender;
    private Date birthDate;

    private String personnummer;

    private List<ObservationDTO> observations;
    private List<ConditionDTO> conditions;
    private List<CommunicationDTO> communications;

    public PatientDTO(String id, String firstName, String lastName, String gender, Date birthDate, String personnummer) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthDate = birthDate;
        this.personnummer = personnummer;
    }

    public PatientDTO() {

    }

    public String getPersonnummer() {
        return personnummer;
    }

    public void setPersonnummer(String personnummer) {
        this.personnummer = personnummer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public List<ObservationDTO> getObservations() {
        return observations;
    }

    public void setObservations(List<ObservationDTO> observations) {
        this.observations = observations;
    }

    public List<ConditionDTO> getConditions() {
        return conditions;
    }

    public void setConditions(List<ConditionDTO> conditions) {
        this.conditions = conditions;
    }

    public List<CommunicationDTO> getCommunications() {
        return communications;
    }

    public void setCommunications(List<CommunicationDTO> communications) {
        this.communications = communications;
    }

}
