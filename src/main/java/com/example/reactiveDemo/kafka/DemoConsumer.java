package com.example.reactiveDemo.kafka;

import com.example.reactiveDemo.model.Quote;
import com.example.reactiveDemo.repository.QuoteMongoReactiveRepository;
import com.tiket.tix.common.monitor.StatsDClientWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverOffset;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

import static com.example.reactiveDemo.kafka.KafkaFactory.CONSUMER_RECEIVE_CONCURRENCY;

@Component
public class DemoConsumer {

    private static final Logger log = LoggerFactory.getLogger(DemoConsumer.class);

    @Value("myTopic")
    private String blockedTopic;

    @Autowired
    private KafkaFactory kafkaFactory;

    @Autowired
    private QuoteMongoReactiveRepository quoteMongoReactiveRepository;

    @Autowired
    private StatsDClientWrapper statsDClientWrapper;

    public Runnable getRunnable() {
        return () -> {
            log.info("Started subscribing to topic: {}", blockedTopic);
            final ReactiveKafkaConsumerTemplate<String, Quote> kafkaConsumerTemplate = kafkaFactory
                    .createKafkaConsumerTemplate(blockedTopic, Quote.class);
            final AtomicLong ai = new AtomicLong();
            kafkaConsumerTemplate.receive()
                    .flatMap(record -> {
//                        long startTime = System.nanoTime();
//                        ai.addAndGet(System.nanoTime());
                        ai.set(System.nanoTime());
//                        ai = new AtomicLong(System.nanoTime());
                        log.info("got in getrunnable for execution");
                        final ReceiverOffset offset = record.receiverOffset();
                        final Quote message = record.value();
                        final String id = message.getId();
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        log.info(
                                "Received message: topic={} topic-partition={}offset={} timestamp={} key={} value={}\n",
                                blockedTopic,
                                offset.topicPartition(),
                                offset.offset(),
                                record.timestamp(),
                                record.key(),
                                message);

                        return quoteMongoReactiveRepository.findById("01000")
                                .delaySubscription(Duration.ofMillis(200))
                                .thenReturn(Mono.defer(offset::commit))
                                .thenReturn(id);
                    }, CONSUMER_RECEIVE_CONCURRENCY)
                    .onErrorResume( e -> {
                        log.error("supplier.scheduleBlocked message not committed: Exception:", e);
                        return Mono.empty();
                    })
                    .doOnNext(x -> {
                        System.out.println("in dofinally: " + x);
//                        long startTime = System.nanoTime();
//                        statsDClientWrapper.monitor("myTopic", Monitor.ServiceGroup.ES, ErrorCode.SUCCEED,
//                                TimeUnit.MILLISECONDS.convert(System.nanoTime() - ai.get(), TimeUnit.NANOSECONDS));
                    })
                    .subscribe();
        };
    }
}

