package com.example.FullstackMessageService.Datalayer.Repositories;

import com.example.FullstackMessageService.Model.Models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IMessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE " +
            "(m.sender.personnummer = :user1 AND m.receiver.personnummer = :user2) OR " +
            "(m.sender.personnummer = :user2 AND m.receiver.personnummer = :user1) " +
            "ORDER BY m.timeStamp ASC")
    List<Message> findAllMessagesBetweenTwoUsers(String user1, String user2);

    @Query("SELECT m FROM Message m WHERE m.sender.personnummer = :personnummer OR m.receiver.personnummer = :personnummer ORDER BY m.timeStamp ASC")
    List<Message> findAllMessagesForUser(@Param("personnummer") String personnummer);
}
