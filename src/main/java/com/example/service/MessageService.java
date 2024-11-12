package com.example.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.exception.InvalidMessageTextException;
import com.example.exception.MessageNotFoundException;
import com.example.exception.NoAssociatedUserException;
import com.example.repository.AccountRepository;
import com.example.repository.MessageRepository;

@Service
@Transactional
public class MessageService {
    private MessageRepository messageRepository;
    private AccountRepository accountRepository;

    public MessageService(MessageRepository messageRepository, AccountRepository accountRepository) {
        this.messageRepository = messageRepository;
        this.accountRepository = accountRepository;
    }

    /**
     * To utilize the message repository and persist a new message
     * @param message the message to be persisted
     * @return the message object including its messageId
     * @throws InvalidMessageTextException when the message text is invalid (blank or longer than 255)
     * @throws NoAssociatedUserException when there is no associated account with the message object.
     */
    public Message createMessage(Message message) throws InvalidMessageTextException, NoAssociatedUserException{
        validateMessage(message);
        return messageRepository.save(message);
    }

    /**
     * Retrieves all messages from the db
     * @return a list of all messages if any.
     */
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    /**
     * To retrieve a message by its id from the database.
     * @param id the messageId
     * @return the message with hte given id if it exists.
     */
    public Optional<Message> getMessageById(int id) {
        return messageRepository.findById(id);
    }

    /**
     * Deletes a message with the given messageId
     * @param messageId
     * @return the number of database rows affected.
     */
    public int deleteMessage(int messageId) {
        return messageRepository.deleteByMessageId(messageId);
    }

    /**
     * Updates the text of the message with the given messageId
     * @param messageId the message of the id to be updated
     * @param messageText the new message text
     * @return the number of database rows affected by the update.
     */
    @Transactional
    public int updateMessageText(int messageId, String messageText) {
        validateMessageText(messageText);
        return messageRepository.updateMessageTextByMessageId(messageId, messageText);
    }

    /**
     * Retrieves all messages posted by the account with the given accountId
     * @param postedBy the accountId
     * @return A list of all messages
     */
    public List<Message> getUserMessages(int postedBy) {
        return messageRepository.findByPostedBy(postedBy).get();
    }

    /**
     * To validate a message object
     * @param message the message to be validated
     * @return true if the message meets all requirements, otherwise throws an exception.
     */
    private boolean validateMessage(Message message) throws NoAssociatedUserException{
        String text = message.getMessageText();
        if (!accountRepository.existsById(message.getPostedBy())) {
            throw new NoAssociatedUserException("No Associated User");
        }
        return validateMessageText(text);
    }

    /**
     * To validate message text
     * @param text the message text to be validated
     * @return true if the text meets all requirements, otherwise throws an InvalidMessageTextException.
     */
    private boolean validateMessageText(String text) throws InvalidMessageTextException{
        if (text.trim().isEmpty()) {
            throw new InvalidMessageTextException("Message text cannot be empty.");
        }
        else if(text.trim().length() > 255) {
            throw new InvalidMessageTextException("Message text is to long. 255 characters max.");
        }
        return true;
    }

}
