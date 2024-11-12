package com.example.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.exception.InvalidMessageTextException;
import com.example.exception.MessageNotFoundException;
import com.example.exception.NoAssociatedUserException;
import com.example.exception.NotUniqueUserException;
import com.example.exception.UnAuthorizedUserException;
import com.example.service.AccountService;
import com.example.service.MessageService;

/**
 * You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
@RestController
@RequestMapping("/")
public class SocialMediaController {

    private final AccountService accountService;
    private final MessageService messageService;

    @Autowired
    public SocialMediaController(AccountService accountService, MessageService messageService) {
        this.accountService = accountService;
        this.messageService = messageService;
    }

    /**
     * Endpoint for registering a new account.
     * @param account the account to be registered.
     * @return HttpResponse with appropriate atatus and body.
     */
    @PostMapping("register")
    public ResponseEntity<Account> register(@RequestBody Account account) {
        Account newAccount = accountService.createAccount(account);

        return newAccount.getAccountId() > 0 ?
                ResponseEntity.ok(newAccount) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    /**
     * Endpoint for loging in.
     * @param account the account that has the username and password
     * @return HttpResponse with appropriate status and body.
     */
    @PostMapping("login")
    public ResponseEntity<Account> login(@RequestBody Account account) {
        Account newAccount = accountService.login(account);
        
        return ResponseEntity.ok(newAccount);
    }

    /**
     * End point for creating a new message.
     * @param message the message to be created.
     * @return an HttpResponse with appropriate status and response body.
     */
    @PostMapping("messages")
    public ResponseEntity<Message> saveMessage(@RequestBody Message message) {
        message = messageService.createMessage(message);
        return ResponseEntity.ok(message);
    }

    /**
     * Endpoint for retrieving all messages.
     * @return HttpResponse with appropraiye status code and response body.
     */
    @GetMapping("messages")
    public ResponseEntity<List<Message>> getAllMessages() {
        List<Message> messages = new ArrayList<>();
        messages = messageService.getAllMessages();
        return ResponseEntity.ok(messages);
    }

    /**
     * Endpoint for retrieving a message with the given messageId.
     * @param messageId the id of the message to be retrieved.
     * @return An HttpResponse with appropriate status code and response body.
     */
    @GetMapping("messages/{messageId}")
    public ResponseEntity<Message> getMessage(@PathVariable int messageId) {
        return messageService.getMessageById(messageId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.ok().build());
    }

    /**
     * Endpoint for deleting a message with the given messageId
     * @param messageId the id of the message to be deleted.
     * @return An HttpResponse with appropriate status code and response body.
     */
    @DeleteMapping("messages/{messageId}")
    public ResponseEntity<Integer> deleteMessagae(@PathVariable int messageId) {
        int affectedRows = messageService.deleteMessage(messageId);
        return affectedRows > 0 ? ResponseEntity.ok(affectedRows) : ResponseEntity.ok().build();
    }

    /**
     * Endpoint for updating the text of the message with the given messageId
     * @param messageId the id of the message to be updated
     * @param requestBody the request body for retrieving the new message text
     * @return An HttpResponse with appropriate status code and response body.
     */
    @PatchMapping("messages/{messageId}")
    public ResponseEntity<Integer> updateMessage(@PathVariable int messageId, @RequestBody Map<String, String> requestBody) {
        String messageText = requestBody.get("messageText");
        int affectedRows = messageService.updateMessageText(messageId, messageText);
        if (affectedRows > 0) {
            return ResponseEntity.ok(affectedRows);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    /**
     * Endpoint to retrieve all messages posted by the account with the given accountId
     * @param accountId the id of the account
     * @return An HttpResponse with appropriate status code and response body.
     */
    @GetMapping("accounts/{accountId}/messages")
    public ResponseEntity<List<Message>> getUserMessages(@PathVariable int accountId) {
        return ResponseEntity.ok(messageService.getUserMessages(accountId));
    }

    @ExceptionHandler(NotUniqueUserException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleNotUniqueUser(NotUniqueUserException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(UnAuthorizedUserException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleUnAuthorizedUser(UnAuthorizedUserException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(NoAssociatedUserException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleNoAssociatedUser(NoAssociatedUserException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(InvalidMessageTextException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInvalidMessageText(InvalidMessageTextException ex) {
        return ex.getMessage();
    }
    @ExceptionHandler(MessageNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInvalidMessageNotFound(MessageNotFoundException ex) {
        return ex.getMessage();
    }
}
