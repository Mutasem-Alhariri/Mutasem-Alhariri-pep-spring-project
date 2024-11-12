package com.example.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer>{
    int deleteByMessageId(Integer messageId);
    Optional<Message> findByMessageId(Integer messageId);

    @Modifying
    @Query("UPDATE Message m SET m.messageText = :messageText WHERE m.messageId = :messageId")
    int updateMessageTextByMessageId(@Param("messageId") Integer messageId, @Param("messageText") String messageText);

    Optional<List<Message>> findByPostedBy(Integer postedBy);
}
