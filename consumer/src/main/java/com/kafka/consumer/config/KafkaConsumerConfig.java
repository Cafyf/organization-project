package com.kafka.consumer.config;

import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringSerializer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.RetryListener;

import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import org.springframework.util.backoff.ExponentialBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class KafkaConsumerConfig {

    @Bean
    public ProducerFactory<String, Object> dltProducerFactory() {

        Map<String, Object> config = new HashMap<>();

        config.put(
                org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "localhost:9092"
        );

        config.put(
                org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class
        );

        config.put(
                org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                JacksonJsonSerializer.class
        );

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, Object> dltKafkaTemplate(
            ProducerFactory<String, Object> dltProducerFactory) {

        return new KafkaTemplate<>(dltProducerFactory);
    }

    @Bean
    public DefaultErrorHandler kafkaErrorHandler(
            KafkaTemplate<String, Object> dltKafkaTemplate) {

        // Send failed messages to Dead Letter Topic
        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(
                        dltKafkaTemplate,
                        (record, exception) ->
                                new TopicPartition(
                                        record.topic() + ".DLT",
                                        record.partition()
                                )
                );

        // 5 seconds interval, 3 retries
        ExponentialBackOffWithMaxRetries backOff =
                new ExponentialBackOffWithMaxRetries(3);

        backOff.setInitialInterval(5000L);
        backOff.setMultiplier(2.0);
        backOff.setMaxInterval(60000L);

        DefaultErrorHandler errorHandler =
                new DefaultErrorHandler(recoverer, backOff);

        // Log every failed attempt
        errorHandler.setRetryListeners(new RetryListener() {

            @Override
            public void failedDelivery(
                    ConsumerRecord<?, ?> record,
                    Exception exception,
                    int deliveryAttempt) {

                log.error(
                        "Kafka message processing failed | " +
                                "Topic: {} | Partition: {} | Offset: {} | " +
                                "Key: {} | Retry Attempt: {} | Error: {}",
                        record.topic(),
                        record.partition(),
                        record.offset(),
                        record.key(),
                        deliveryAttempt,
                        exception.getMessage(),
                        exception
                );
            }
        });

        return errorHandler;
    }
}
