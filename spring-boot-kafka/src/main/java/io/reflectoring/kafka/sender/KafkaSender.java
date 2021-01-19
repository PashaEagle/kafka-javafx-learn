package io.reflectoring.kafka.sender;

import io.reflectoring.kafka.dto.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaSender {

    private final Logger LOG = LoggerFactory.getLogger(KafkaSender.class);

    private KafkaTemplate<String, Message> userKafkaTemplate;

    @Autowired
    KafkaSender(KafkaTemplate<String, Message> userKafkaTemplate) {
        this.userKafkaTemplate = userKafkaTemplate;
    }

    public void sendCustomMessage(Message message, String topicName) {
        LOG.info("Kafka sender pushed: {}", message);
        LOG.info("--------------------------------");

        userKafkaTemplate.send(topicName, message);
    }

}
