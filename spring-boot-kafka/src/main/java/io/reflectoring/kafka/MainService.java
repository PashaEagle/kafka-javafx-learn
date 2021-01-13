package io.reflectoring.kafka;

import io.reflectoring.kafka.dto.Message;
import io.reflectoring.kafka.sender.KafkaSenderExample;
import io.reflectoring.kafka.sender.KafkaSenderWithMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MainService {

    @Value("${io.reflectoring.kafka.topic-1}")
    private String topic1;

    @Autowired
    private KafkaSenderExample kafkaSenderExample;
    @Autowired
    private KafkaSenderWithMessageConverter messageConverterSender;
    @Autowired
    private Map<String, Map<String, List<Message>>> userToRecipientToMessagesMap;

    public Message sendMessage(Message message) {
        kafkaSenderExample.sendCustomMessage(message, topic1);
        return message;
    }

    public Map<String, List<Message>> getAllUserMessages(String from) {
        return userToRecipientToMessagesMap.get(from);
    }
}