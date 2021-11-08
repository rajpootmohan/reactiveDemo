package com.example.reactiveDemo.kafka;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.kafka.support.converter.MessagingMessageConverter;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.SenderOptions;

import java.util.Collections;
import java.util.Map;



@Configuration
public class KafkaFactory {

/*
   * At least once semantics: If a producer ack times out or receives an error, it might retry
   * sending the message assuming that the message was not written to the Kafka topic. If the broker
   * had failed right before it sent the ack but after the message was successfully written to the
   * Kafka topic, this retry leads to the message being written twice and hence delivered more than
   * once to the end consumer.
   * <p>
   * Producer is configured to send at least once. So we must make sure that we de-duplicate message
   * in each consumer to prevent double processing.
*/


  public static final String INVENTORY_CONSUMER_GROUP_LABEL = ".inventory.group";
  public static final String ORDER_CONSUMER_GROUP_LABEL = ".order.group";
  public static final String SUPPLIER_CONSUMER_GROUP_LABEL = ".supplier.group";
  public static final Integer CONSUMER_RECEIVE_CONCURRENCY = 1;


  @Value("${events.kafka.bootstrapServers}")
  private String bootstrapServers;
  @Value("${events.kafka.consumer.offsetReset}")
  private String consumerOffsetReset;
  @Value("${events.kafka.consumer.enableAutoCommit}")
  private String consumerEnableAutoCommit;
  @Value("${events.kafka.consumer.sessionTimeout}")
  private String consumerSessionTimeout;
  @Value("${events.kafka.consumer.heartbeatInterval}")
  private String consumerheartbeatInterval;

  @Value("${events.kafka.producer.enableIdempotence}")
  private String producerEnableIdempotence;
  @Value("${events.kafka.producer.acks}")
  private String producerAcks;

  public ReactiveKafkaConsumerTemplate createKafkaConsumerTemplate(String topic,
                                                                   Class<?> valueType) {
    if (StringUtils.isBlank(topic) || valueType == null) {
      return null;
    }

    final Map<String, Object> props = Maps.newHashMap();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, topic + ORDER_CONSUMER_GROUP_LABEL);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
    props.put(JsonDeserializer.REMOVE_TYPE_INFO_HEADERS, false);
    props.put(JsonDeserializer.KEY_DEFAULT_TYPE, String.class);
    props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, valueType);
    props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, consumerOffsetReset);
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, consumerEnableAutoCommit);
    props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, consumerSessionTimeout);
    props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, consumerheartbeatInterval);

    final ReceiverOptions<Object, Object> receiverOptions = ReceiverOptions.create(props)
        .subscription(Collections.singleton(topic));

    return new ReactiveKafkaConsumerTemplate<>(receiverOptions);
  }


  @Primary
  @Bean("primaryKafkaProducer")
  public ReactiveKafkaProducerTemplate createKafkaProducerTemplate() {
    final Map<String, Object> props = Maps.newConcurrentMap();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

    props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, producerEnableIdempotence);
    props.put(ProducerConfig.ACKS_CONFIG, producerAcks);

    final SenderOptions<Integer, String> senderOptions = SenderOptions.create(props);
    return new ReactiveKafkaProducerTemplate<>(senderOptions,
        new MessagingMessageConverter());
  }
}
