package io.reflectoring.kafka.data;

import io.reflectoring.kafka.dto.ChatId;
import io.reflectoring.kafka.dto.Message;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class MessagesDataConfig {

    @Bean
    public Map<ChatId, List<Message>> chatIdToMessagesMap() {
        return new HashMap<>();
    }

    @Bean
    public Map<String, List<String>> usernameToChattersMap() {
        return new HashMap<>();
    }

    @Bean
    public Map<String, List<Integer>> loggedUsernameToClientPorts() {
        return new HashMap<>();
    }
}
