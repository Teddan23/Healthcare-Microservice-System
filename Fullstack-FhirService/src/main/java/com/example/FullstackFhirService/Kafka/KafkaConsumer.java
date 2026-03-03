package com.example.FullstackFhirService.Kafka;

import com.example.FullstackFhirService.Controller.DTOs.UserDTO;
import com.example.FullstackFhirService.Model.Services.PatientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class KafkaConsumer {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PatientService patientService;

    @KafkaListener(topics = "patient-topic")
    public void consumeUserMessage(String message) {
        System.out.println("I RECEIVED KAFKA");

        try {
            UserDTO userDto = convertMessageToUserDTO(message);

            System.out.println("Received Kafka message for user: " + userDto);

            patientService.addPatientToHapiWithUser(userDto);
        } catch (Exception e) {
            System.out.println("ERROR WHEN RECEIVING USER! SEE ERROR BELOW");
            e.printStackTrace();
        }
    }

    private UserDTO convertMessageToUserDTO(String message) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        Map<String, String> userMap = objectMapper.readValue(message, Map.class);

        String birthDateString = userMap.get("birthDate");
        if (birthDateString == null) {
            System.out.println("BirthDate is null!!");
            throw new IllegalArgumentException("birthDate cannot be null");
        }
        LocalDate birthDate = LocalDate.parse(birthDateString, formatter);

        String personnummer = userMap.get("personnummer");
        String firstName = userMap.get("firstName");
        String lastName = userMap.get("lastName");
        String gender = userMap.get("gender");
        String role = userMap.get("role");

        if (personnummer == null || firstName == null || lastName == null || gender == null || role == null) {
            throw new IllegalArgumentException("Missing required fields in message");
        }

        UserDTO userDto = new UserDTO(personnummer, firstName, lastName, gender, birthDate, role);

        System.out.println("FINALY!!!! USERDTO IS: " + userDto);

        return userDto;
    }
}


