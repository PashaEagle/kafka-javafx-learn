package io.reflectoring.kafka.controller;

import io.reflectoring.kafka.MainService;
import io.reflectoring.kafka.dto.Message;
import io.reflectoring.kafka.dto.SendMessageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired
    private MainService service;

    @PostMapping("/send")
    Message sendMessage(@RequestBody SendMessageRequest sendMessageRequest) {
        LOG.info("Received request to send new message: {}", sendMessageRequest);
        return service.sendMessage(sendMessageRequest);
    }

    @GetMapping("/messages/{username}")
    Map<String, List<Message>> getUserMessages(@PathVariable String username, @RequestParam Integer clientPort) {
        LOG.info("Received request to get all messages for user: {} for client with port: {}", username, clientPort);
        service.addLoggedClient(username, clientPort);
        return service.getAllUserMessages(username);
    }

    @DeleteMapping("/logout")
    void logoutClient(@RequestParam String username, @RequestParam Integer port) {
        LOG.info("Received request to make a logout for user: {} and client with port: {}", username, port);
        service.logoutClient(username, port);
    }
}
