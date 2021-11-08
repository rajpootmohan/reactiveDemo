package com.example.reactiveDemo.controller;

import com.example.reactiveDemo.model.Quote;
import com.example.reactiveDemo.repository.QuoteMongoReactiveRepository;
import com.tiket.tix.common.monitor.StatsDClientWrapper;
import com.tiket.tix.common.monitor.aspects.Monitor;
import com.tiket.tix.common.monitor.enums.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@RestController
public class QuoteReactiveController {

    private static final int DELAY_PER_ITEM_MS = 100;

    private final QuoteMongoReactiveRepository quoteMongoReactiveRepository;

    @Autowired
    private StatsDClientWrapper statsDClientWrapper;

    public QuoteReactiveController(final QuoteMongoReactiveRepository quoteMongoReactiveRepository) {
        this.quoteMongoReactiveRepository = quoteMongoReactiveRepository;
    }

    @GetMapping("/quotes-reactive")
    public Flux<Quote> getQuoteFlux() {
        return getQuoteFluxMiddleWare().delayElements(Duration.ofMillis(DELAY_PER_ITEM_MS));
//         return quoteMongoReactiveRepository.findAll().take(4).delayElements(Duration.ofMillis(DELAY_PER_ITEM_MS));
    }


    @GetMapping("/quotes-reactive-paged")
    @Monitor(name="getQuoteFlux", metricGroup = Monitor.ServiceGroup.API_IN)
    public Flux<Quote> getQuoteFlux(final @RequestParam(name = "page") int page,
                                    final @RequestParam(name = "size") int size) {
        System.out.println("came in controller");
        throw new RuntimeException("tmp");
//        return quoteMongoReactiveRepository.findAllByIdNotNullOrderByIdAsc(PageRequest.of(page, size))
//
//                .delayElements(Duration.ofMillis(DELAY_PER_ITEM_MS));
    }

    public Flux<Quote> getQuoteFluxMiddleWare() {
        final Long startTime = System.currentTimeMillis();
        System.out.println("started getQuoteFluxMiddleWare");
//        AtomicReference<Long> startTime = new AtomicReference<>();
        return quoteMongoReactiveRepository
                .findAll()
//                .doOnSubscribe(x -> )
                .take(5l)
                .doFinally(z -> System.out.println("sometihng"))
                .doFinally(x -> {
                    System.out.println(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime));
                    statsDClientWrapper.monitor("getQuote", Monitor.ServiceGroup.KAFKA_CONSUMER, ErrorCode.SUCCEED,
                        TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime));
                })
                ;

    }

}
// x -> statsDClientWrapper.monitor("getQuote", Monitor.ServiceGroup.API_IN, ErrorCode.SUCCEED,
//                        TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime)

//.delayElements(Duration.ofMillis(DELAY_PER_ITEM_MS));