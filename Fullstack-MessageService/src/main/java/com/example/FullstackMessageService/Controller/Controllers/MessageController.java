package com.example.FullstackMessageService.Controller.Controllers;

import com.example.FullstackMessageService.Controller.DTOs.MessageDTO;
import com.example.FullstackMessageService.Controller.DTOs.SendMessageDTO;
import com.example.FullstackMessageService.Model.Services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
@CrossOrigin(origins = "*")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PreAuthorize("hasRole('DOCTOR') or hasRole('STAFF') or hasRole('PATIENT')")
    @PostMapping("/sendMessage")
    public void sendMessage(@RequestBody SendMessageDTO SendmessageDTO){
        System.out.println("In send message");
        if(messageService.sendMessage(SendmessageDTO)){
            System.out.println("Message sent!");
        }
        else{
            System.out.println("Could not send message!");
        }
    }

    @PreAuthorize("hasRole('DOCTOR') or hasRole('STAFF') or hasRole('PATIENT')")
    @PostMapping("/getMessages")
    public List<MessageDTO> getMessages(String patientPersonnummer){
        return messageService.getAllMessagesForAPatient(patientPersonnummer);
    }


}
