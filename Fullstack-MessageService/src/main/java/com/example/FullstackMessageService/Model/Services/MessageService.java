package com.example.FullstackMessageService.Model.Services;

import com.example.FullstackMessageService.Controller.DTOs.MessageDTO;
import com.example.FullstackMessageService.Controller.DTOs.SendMessageDTO;
import com.example.FullstackMessageService.Controller.DTOs.UserDTO;
import com.example.FullstackMessageService.Datalayer.Repositories.IMessageRepository;
import com.example.FullstackMessageService.Model.Models.Message;
import com.example.FullstackMessageService.Model.Models.User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

import static com.example.FullstackMessageService.Model.Models.User.fromDTO;

@Service
public class MessageService {

    private final IMessageRepository messageRepository;

    private final RestTemplate restTemplate;

    private static final String USER_SERVICE_URL = "https://fullstack-userservice.app.cloud.cbh.kth.se";

    public MessageService(IMessageRepository messageRepository, RestTemplate restTemplate) {
        this.messageRepository = messageRepository;
        this.restTemplate = restTemplate;
    }

    public boolean sendMessage(SendMessageDTO sendMessageDto) {
        if (sendMessageDto == null) {
            return false;
        }

        System.out.println("Getting sender");
        UserDTO sender = fetchUser(sendMessageDto.getSenderPersonnummer());

        if(sender == null){
            System.out.println("sender is null. CRASHING!");
            throw new IllegalStateException("SENDER EXISTERAR INTET!");
        }

        System.out.println("Getting receiver");
        UserDTO receiver = fetchUser(sendMessageDto.getReceiverPersonnummer());

        if(receiver == null){
            System.out.println("Receiver is null. CRASHING!");
            throw new IllegalStateException("RECEIVER EXISTERAR INTE!");
        }

        Message message = new Message();

        message.setMessage(sendMessageDto.getMessage());
        message.setReceiver(fromDTO(receiver));
        message.setSender(fromDTO(sender));
        message.setTimeStamp(sendMessageDto.getTimeStamp());

        messageRepository.save(message);

        System.out.println("Returning true!");

        return true;
    }

    public List<MessageDTO> getAllMessagesBetweenTwoUsers(UserDTO currentUser, UserDTO otherUser){
        if (currentUser == null || otherUser == null) {
            throw new IllegalArgumentException("Users cannot be null");
        }

        List<Message> messages = messageRepository.findAllMessagesBetweenTwoUsers(
                currentUser.getPersonnummer(),
                otherUser.getPersonnummer()
        );

        List<MessageDTO> dtoList = new ArrayList<>();

        for(Message m : messages){
            dtoList.add(m.toDTO());
        }

        return dtoList;

    }

    public List<MessageDTO> getAllMessagesForAPatient(String patientPersonnummer){
        if (patientPersonnummer == null || patientPersonnummer.isBlank()) {
            throw new IllegalArgumentException("Patient personnummer cannot be null or blank");
        }

        List<Message> messages = messageRepository.findAllMessagesForUser(patientPersonnummer);

        List<MessageDTO> dtoList = new ArrayList<>();

        for(Message m : messages){
            dtoList.add(m.toDTO());
        }

        return dtoList;
    }

    private UserDTO fetchUser(String personnummer) {
        String url = UriComponentsBuilder.fromHttpUrl(USER_SERVICE_URL + "/user/fetchUser")
                .queryParam("personnummer", personnummer)
                .toUriString();

        try {
            return restTemplate.getForObject(url, UserDTO.class);
        } catch (Exception e) {
            System.err.println("Error fetching user: " + e.getMessage());
            return null;
        }
    }
}
