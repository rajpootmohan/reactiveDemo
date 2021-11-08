package com.example.reactiveDemo.kafka;

import com.example.reactiveDemo.model.Quote;
import com.tiket.tix.common.monitor.aspects.MonitorAspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class DemoProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DemoProducer.class);

    @Value("myTopic")
    private String blockedTopic;

    @Autowired
    private ReactiveKafkaProducerTemplate kafkaProducerTemplate;

    public Mono<Void> produce() {
        Quote quote = new Quote("1", "simple description", "deatailed desctiption");
        return kafkaProducerTemplate.send(blockedTopic, quote)
                .doOnSuccess(
                        s -> LOGGER.info("sendBlocking: Successfully sent transactionMessage: {}",
                                quote))
                .doOnError(
                        o -> LOGGER.error("sendBlocking: Failed sending transactionMessage: {}",
                                quote))
                .then();
    }
}
