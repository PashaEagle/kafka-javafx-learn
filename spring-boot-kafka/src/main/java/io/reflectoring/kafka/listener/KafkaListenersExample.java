package io.reflectoring.kafka.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reflectoring.kafka.MainService;
import io.reflectoring.kafka.dto.ChatId;
import io.reflectoring.kafka.dto.Message;
import io.reflectoring.kafka.dto.SendMessageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
public class KafkaListenersExample {

    private final Logger LOG = LoggerFactory.getLogger(KafkaListenersExample.class);

    @Autowired
    private Map<ChatId, List<Message>> chatIdToMessagesMap;

    @Autowired
    private Map<String, List<String>> usernameToChattersMap;

    @Autowired
    private Map<String, List<Integer>> loggedUsernameToClientPorts;

    @Autowired
    private MainService mainService;

    //	@KafkaListener(topics = "reflectoring-1")
//    @KafkaListener(id = "thing2")
//    public void listener(String message) {
//        LOG.info("Listener [{}]", message);
//    }

//	@KafkaListener(topics = { "reflectoring-1", "reflectoring-2" }, groupId = "reflectoring-group-2")
//	void commonListenerForMultipleTopics(String message) {
//		LOG.info("MultipleTopicListener - [{}]", message);
//	}
//
//	@KafkaListener(topicPartitions = @TopicPartition(topic = "reflectoring-3", partitionOffsets = {
//			@PartitionOffset(partition = "0", initialOffset = "0") }), groupId = "reflectoring-group-3")
//	void listenToParitionWithOffset(@Payload String message, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
//			@Header(KafkaHeaders.OFFSET) int offset) {
//		LOG.info("ListenToPartitionWithOffset [{}] from partition-{} with offset-{}", message, partition, offset);
//	}
//
//	@KafkaListener(topics = "reflectoring-bytes")
//	void listenerForRoutingTemplate(String message) {
//		LOG.info("RoutingTemplate BytesListener [{}]", message);
//	}
//
//	@KafkaListener(topics = "reflectoring-others")
//	@SendTo("reflectoring-2")
//	String listenAndReply(String message) {
//		LOG.info("ListenAndReply [{}]", message);
//		return "This is a reply sent to 'reflectoring-2' topic after receiving message at 'reflectoring-others' topic";
//	}

    @KafkaListener(id = "1", topics = "messages-topic", containerFactory = "kafkaJsonListenerContainerFactory", topicPartitions =
            {
                    @TopicPartition(topic = "messages-topic",
                            partitionOffsets = @PartitionOffset(partition = "0", initialOffset = "0"))
            })
    void listenerWithMessageConverter(Message message) {
        LOG.info("MessageObjectListener [{}]", message);
        ChatId chatId = new ChatId(message.getFrom(), message.getTo());
        List<Message> messages = chatIdToMessagesMap.get(chatId);
        if (messages == null) {
            messages = new ArrayList<>();
        }
        messages.add(message);
        chatIdToMessagesMap.put(chatId, messages);
        LOG.info(chatIdToMessagesMap.toString());
        createNewChatIfNotPresent(message.getFrom(), message.getTo());
        LOG.info("Successfully added new message");
        List<Integer> clientPortsForFrom = loggedUsernameToClientPorts.get(message.getFrom());
        if (clientPortsForFrom == null) clientPortsForFrom = Collections.emptyList();
        List<Integer> clientPortsForTo = loggedUsernameToClientPorts.get(message.getTo());
        if (clientPortsForTo == null) clientPortsForTo = Collections.emptyList();
        clientPortsForFrom.forEach(port -> {
            Map<String, List<Message>> userMessages = mainService.getAllUserMessages(message.getFrom());
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String body;
            try {
                body = new ObjectMapper().writeValueAsString(userMessages);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return;
            }
            HttpEntity<String> request =
                    new HttpEntity<String>(body, headers);

            String personResultAsJsonStr =
                    restTemplate.postForObject("http://localhost:" + port + "/test", request, String.class);
        });

        clientPortsForTo.forEach(port -> {
            Map<String, List<Message>> userMessages = mainService.getAllUserMessages(message.getTo());
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String body;
            try {
                body = new ObjectMapper().writeValueAsString(userMessages);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return;
            }
            HttpEntity<String> request =
                    new HttpEntity<String>(body, headers);

            String personResultAsJsonStr =
                    restTemplate.postForObject("http://localhost:" + port + "/test", request, String.class);
        });

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
