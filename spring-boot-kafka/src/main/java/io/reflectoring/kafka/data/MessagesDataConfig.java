package io.reflectoring.kafka.data;

import io.reflectoring.kafka.dto.Message;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class MessagesDataConfig {

    @Bean
    public Map<String, Map<String, List<Message>>> userToRecipientToMessagesMap() {
        return new HashMap<>();
    }
}
