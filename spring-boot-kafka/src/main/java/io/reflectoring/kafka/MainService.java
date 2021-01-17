package io.reflectoring.kafka;

import io.reflectoring.kafka.dto.ChatId;
import io.reflectoring.kafka.dto.Message;
import io.reflectoring.kafka.dto.SendMessageRequest;
import io.reflectoring.kafka.sender.KafkaSenderExample;
import io.reflectoring.kafka.sender.KafkaSenderWithMessageConverter;
import org.apache.kafka.common.network.Send;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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
    private Map<ChatId, List<Message>> chatIdToMessagesMap;
    @Autowired
    private Map<String, List<String>> usernameToChattersMap;

    public Message sendMessage(SendMessageRequest sendMessageRequest) {
        Message message = Message.fromRequest(sendMessageRequest);
        message.setTimestamp(System.currentTimeMillis());
        kafkaSenderExample.sendCustomMessage(message, topic1);
        return message;
    }

    public Map<String, List<Message>> getAllUserMessages(String username) {
        List<String> chatters = usernameToChattersMap.get(username);
        Map<String, List<Message>> chatterToMessagesMap = new HashMap<>();
        chatters.forEach(chatter -> {
            ChatId tempChat = new ChatId(username, chatter);
            List<Message> messagesWithChatter = chatIdToMessagesMap.get(tempChat);
            chatterToMessagesMap.put(chatter, messagesWithChatter);
        });
        return chatterToMessagesMap;
    }
}
