    package com.example.FullstackUserService.Kafka;

    import com.fasterxml.jackson.databind.ObjectMapper;
    import com.example.FullstackUserService.Controller.DTOs.UserDTO;
    import com.fasterxml.jackson.databind.SerializationFeature;
    import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
    import org.springframework.context.annotation.Bean;
    import org.springframework.kafka.core.KafkaTemplate;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;

    import java.time.format.DateTimeFormatter;
    import java.util.HashMap;
    import java.util.Map;

    @Service
    public class KafkaProducer {

        @Autowired
        private KafkaTemplate<String, String> kafkaTemplate;

        private String topic = "patient-topic";

        public void sendUserToKafka(UserDTO userDto) {
            System.out.println("In sendUserToKafke method");
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());
                objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

                Map<String, String> userMap = convertUserToMap(userDto);

                System.out.println("Skapade usern som en map");

                String jsonString = objectMapper.writeValueAsString(userMap);

                System.out.println("Created jsonString. will now try to send to kafka");
                
                kafkaTemplate.send(topic, jsonString);
                System.out.println("Sent user to Kafka: " + jsonString);
            } catch (Exception e) {
                System.out.println("DET BLEV FEL! HÄR ÄR FELET");
                e.printStackTrace();
            }
        }

        private Map<String, String> convertUserToMap(UserDTO userDto) {
            Map<String, String> userMap = new HashMap<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            userMap.put("personnummer", userDto.getPersonnummer());
            userMap.put("firstName", userDto.getFirstName());
            userMap.put("lastName", userDto.getLastName());
            userMap.put("gender", userDto.getGender());
            userMap.put("birthDate", userDto.getBirthDate().toString());
            userMap.put("role", userDto.getRole());

            return userMap;
        }
    }
