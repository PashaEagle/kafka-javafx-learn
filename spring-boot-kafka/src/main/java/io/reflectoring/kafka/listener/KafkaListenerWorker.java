package io.reflectoring.kafka.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.reflectoring.kafka.MainService;
import io.reflectoring.kafka.dto.ChatId;
import io.reflectoring.kafka.dto.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class KafkaListenerWorker {

    private final Logger LOG = LoggerFactory.getLogger(KafkaListenerWorker.class);
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${client.webhook.url}")
    private String clientWebhookUrl;
    @Autowired
    private Map<ChatId, List<Message>> chatIdToMessagesMap;
    @Autowired
    private Map<String, List<String>> usernameToChattersMap;
    @Autowired
    private Map<String, List<Integer>> loggedUsernameToClientPorts;
    @Autowired
    private MainService mainService;

    @KafkaListener(id = "1", topics = "messages-topic", containerFactory = "kafkaJsonListenerContainerFactory", topicPartitions = {
            @TopicPartition(topic = "messages-topic", partitionOffsets = @PartitionOffset(partition = "0", initialOffset = "0"))
    })
    void listenerWithMessageConverter(Message message) {
        LOG.info("Kafka listener received: {}", message);
        ChatId chatId = new ChatId(message.getFrom(), message.getTo());
        List<Message> messages = chatIdToMessagesMap.get(chatId);
        if (messages == null) {
            messages = new ArrayList<>();
        }
        messages.add(message);
        chatIdToMessagesMap.put(chatId, messages);
        createNewChatIfNotPresent(message.getFrom(), message.getTo());

        List<Integer> clientPortsForFrom = loggedUsernameToClientPorts.get(message.getFrom());
        if (clientPortsForFrom == null) clientPortsForFrom = Collections.emptyList();
        clientPortsForFrom.forEach(port -> {
            Map<String, List<Message>> userMessages = mainService.getAllUserMessages(message.getFrom());
            postNewMessageToClients(port, userMessages);
        });

        List<Integer> clientPortsForTo = loggedUsernameToClientPorts.get(message.getTo());
        if (clientPortsForTo == null) clientPortsForTo = Collections.emptyList();
        clientPortsForTo.forEach(port -> {
            Map<String, List<Message>> userMessages = mainService.getAllUserMessages(message.getTo());
            postNewMessageToClients(port, userMessages);
        });
    }

    private void postNewMessageToClients(Integer port, Map<String, List<Message>> userMessages) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String body = new ObjectMapper().writeValueAsString(userMessages);
            HttpEntity<String> request = new HttpEntity<>(body, headers);
            restTemplate.postForObject(String.format(clientWebhookUrl, port), request, String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void createNewChatIfNotPresent(String username1, String username2) {
        usernameToChattersMap.computeIfAbsent(username1, k -> new ArrayList<>());
        usernameToChattersMap.computeIfAbsent(username2, k -> new ArrayList<>());

        if (!usernameToChattersMap.get(username1).contains(username2)) {
            List<String> chatters = usernameToChattersMap.get(username1);
            chatters.add(username2);
            usernameToChattersMap.put(username1, chatters);
        }
        if (!usernameToChattersMap.get(username2).contains(username1)) {
            List<String> chatters = usernameToChattersMap.get(username2);
            chatters.add(username1);
            usernameToChattersMap.put(username2, chatters);
        }
    }
}
