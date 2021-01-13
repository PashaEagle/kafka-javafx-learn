package io.reflectoring.kafka;

import io.reflectoring.kafka.dto.Message;
import io.reflectoring.kafka.sender.KafkaSenderExample;
import io.reflectoring.kafka.sender.KafkaSenderWithMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MainService {

    @Value("${io.reflectoring.kafka.topic-1}")
    private String topic1;

    @Autowired
    private KafkaSenderExample kafkaSenderExample;

    @Autowired
    private KafkaSenderWithMessageConverter messageConverterSender;

    public Message sendMessage(Message message) {
        kafkaSenderExample.sendCustomMessage(message, topic1);
        return message;
    }
}
