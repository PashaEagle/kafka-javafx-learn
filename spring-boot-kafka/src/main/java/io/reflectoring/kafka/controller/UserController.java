package io.reflectoring.kafka.controller;

import io.reflectoring.kafka.MainService;
import io.reflectoring.kafka.dto.SendMessageRequest;
import io.reflectoring.kafka.sender.KafkaSenderExample;
import io.reflectoring.kafka.sender.KafkaSenderWithMessageConverter;
import io.reflectoring.kafka.dto.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
        return service.sendMessage(sendMessageRequest);
    }

    @GetMapping("/messages/{username}")
    Map<String, List<Message>> getUserMessages(@PathVariable String username, @RequestParam Integer clientPort) {
        service.addLoggedClient(username, clientPort);
        return service.getAllUserMessages(username);
    }
}
