package com.example.FullstackUserService.Model.Services;

import com.example.FullstackUserService.Controller.DTOs.UserDTO;
import com.example.FullstackUserService.Datalayer.Repositories.IUserRepository;

import com.example.FullstackUserService.Kafka.KafkaProducer;
import com.example.FullstackUserService.Model.Models.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

//import static com.example.FullstackUserService.Model.Models.FHIRPatientConverter.convertUserDTOToPatient;

@Service
public class UserService {
    private final IUserRepository userRepository;
    private final RestTemplate restTemplate;

    @Autowired
    private KafkaProducer kafkaProducer;

    private static final String FHIR_SERVICE_URL = "https://fullstack-fhirservice.app.cloud.cbh.kth.se";

    public UserService(IUserRepository userRepository, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
    }

    public UserDTO getUserByPersonnummer(String personnummer){
        User user = userRepository.findByPersonnummer(personnummer);
        if (user != null) {
            return user.toDTO();
        } else {
            System.out.println("User not found with personnummer: " + personnummer);
            return null;
        }
    }

    public boolean addUser(UserDTO userDTO, String password) {

        if(userDTO == null){
            System.out.println("UserDTO is null!");
            return false;
        }


        if(getUserByPersonnummer(userDTO.getPersonnummer()) != null){
            System.out.println("User already exists with personnummer: " + userDTO.getPersonnummer());
            return false;
        }

        if(userDTO.getRole().equalsIgnoreCase("patient")){
            String url = UriComponentsBuilder.fromHttpUrl(FHIR_SERVICE_URL + "/api/patient/addPatient")
                    .queryParam("userDto", userDTO)
                    .toUriString();
            try {
                kafkaProducer.sendUserToKafka(userDTO);
                System.out.println("Skickade user patient till kafka");
            } catch (Exception e) {
                System.out.println("Could not add user to fhirDB DB");
                System.err.println("Error converting user data to JSON or sending to Kafka: " + e.getMessage());
                return false;
            }
        }
        else{
            System.out.println("User role is not patient, so not adding to HAPI FHIR.");
        }

        User user = User.fromDTO(userDTO);

        user.setPassword(password);

        userRepository.save(user);

        return true;
    }

    public UserDTO handleLogin(String personnummer, String password){
        User user = userRepository.findByPersonnummer(personnummer);

        if(user != null){
            //In cloud the application use keycloak, but for local tests we use .equals()
            if(user.getPassword().equals(password)){
                return user.toDTO();
            }
            else{
                throw new RuntimeException("Wrong password. It is not: " + password);
            }
        }
        else{
            throw new RuntimeException("User not found with personnummer: " + personnummer);
        }
    }

}

