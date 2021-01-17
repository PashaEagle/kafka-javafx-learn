package io.reflectoring.kafka.listener;

import io.reflectoring.kafka.dto.ChatId;
import io.reflectoring.kafka.dto.Message;
import io.reflectoring.kafka.dto.SendMessageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class KafkaListenersExample {

    private final Logger LOG = LoggerFactory.getLogger(KafkaListenersExample.class);

    @Autowired
    private Map<ChatId, List<Message>> chatIdToMessagesMap;

    @Autowired
    private Map<String, List<String>> usernameToChattersMap;

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
