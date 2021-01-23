package io.reflectoring.kafka;

import io.reflectoring.kafka.dto.ChatId;
import io.reflectoring.kafka.dto.Message;
import io.reflectoring.kafka.dto.SendMessageRequest;
import io.reflectoring.kafka.sender.KafkaSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MainService {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Value("${io.reflectoring.kafka.topic-1}")
    private String topic1;

    @Autowired
    private KafkaSender kafkaSender;
    @Autowired
    private Map<ChatId, List<Message>> chatIdToMessagesMap;
    @Autowired
    private Map<String, List<String>> usernameToChattersMap;
    @Autowired
    private Map<String, List<Integer>> loggedUsernameToClientPorts;

    public String getTopic1() {
        return topic1;
    }

    public void setTopic1(String topic1) {
        this.topic1 = topic1;
    }

    public KafkaSender getKafkaSender() {
        return kafkaSender;
    }

    public void setKafkaSender(KafkaSender kafkaSender) {
        this.kafkaSender = kafkaSender;
    }

    public Map<ChatId, List<Message>> getChatIdToMessagesMap() {
        return chatIdToMessagesMap;
    }

    public void setChatIdToMessagesMap(Map<ChatId, List<Message>> chatIdToMessagesMap) {
        this.chatIdToMessagesMap = chatIdToMessagesMap;
    }

    public Map<String, List<String>> getUsernameToChattersMap() {
        return usernameToChattersMap;
    }

    public void setUsernameToChattersMap(Map<String, List<String>> usernameToChattersMap) {
        this.usernameToChattersMap = usernameToChattersMap;
    }

    public Map<String, List<Integer>> getLoggedUsernameToClientPorts() {
        return loggedUsernameToClientPorts;
    }

    public void setLoggedUsernameToClientPorts(Map<String, List<Integer>> loggedUsernameToClientPorts) {
        this.loggedUsernameToClientPorts = loggedUsernameToClientPorts;
    }

    public Message sendMessage(SendMessageRequest sendMessageRequest) {
        Message message = Message.fromRequest(sendMessageRequest);
        message.setTimestamp(System.currentTimeMillis());
        kafkaSender.sendCustomMessage(message, topic1);
        return message;
    }

    public Map<String, List<Message>> getAllUserMessages(String username) {
        List<String> chatters = usernameToChattersMap.get(username);
        Map<String, List<Message>> chatterToMessagesMap = new HashMap<>();
        if (chatters == null) {
            chatters = new ArrayList<>();
        }
        chatters.forEach(chatter -> {
            ChatId tempChat = new ChatId(username, chatter);
            List<Message> messagesWithChatter = chatIdToMessagesMap.get(tempChat);
            chatterToMessagesMap.put(chatter, messagesWithChatter);
        });
        return chatterToMessagesMap;
    }

    public void addLoggedClient(String username, Integer clientPort) {
        List<Integer> clientPorts = loggedUsernameToClientPorts.get(username);
        if (clientPorts == null) clientPorts = new ArrayList<>();
        boolean alreadyPresent = clientPorts.stream().anyMatch(port -> port.equals(clientPort));
        if (!alreadyPresent) {
            clientPorts.add(clientPort);
            loggedUsernameToClientPorts.put(username, clientPorts);
        }
        LOG.info("Current active client map: {}", loggedUsernameToClientPorts);
    }

    public void logoutClient(String username, Integer port) {
        List<Integer> clientPorts = loggedUsernameToClientPorts.get(username);
        clientPorts.remove(port);
        if (!clientPorts.isEmpty())
            loggedUsernameToClientPorts.put(username, clientPorts);
        else
            loggedUsernameToClientPorts.remove(username);
        LOG.info("Client with username={} on port={} successfully logged out", username, port);
    }

}
