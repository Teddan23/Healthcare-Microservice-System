package com.example.FullstackUserService.Model.Models;

import com.example.FullstackUserService.Controller.DTOs.UserDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
public class User {

    @Id
    @NotNull
    private String personnummer;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    private String gender;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @NotNull
    private String password;

    @NotNull
    private String role;


    public String getPersonnummer() {
        return personnummer;
    }

    public void setPersonnummer(String personnummer) {
        this.personnummer = personnummer;
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

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public UserDTO toDTO() {
        return new UserDTO(
                this.personnummer,
                this.firstName,
                this.lastName,
                this.gender,
                this.birthDate,
                this.role
        );
    }

    public static User fromDTO(UserDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("UserDTO cannot be null.");
        }

        User user = new User();
        user.setPersonnummer(dto.getPersonnummer());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setGender(dto.getGender());
        user.setBirthDate(dto.getBirthDate());
        user.setRole(dto.getRole());

        return user;
    }

}
