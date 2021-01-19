package io.reflectoring.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${io.reflectoring.kafka.topic-1}")
    private String topic1;

    @Bean
    NewTopic topic1() {
        return TopicBuilder.name(topic1).build();
    }
}
