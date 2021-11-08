package com.example.reactiveDemo.controller;

import com.example.reactiveDemo.kafka.DemoProducer;
import com.example.reactiveDemo.model.Quote;
//import com.example.reactiveDemo.kafka.DemoProducer;
import com.example.reactiveDemo.repository.QuoteMongoBlockingRepository;
import com.example.reactiveDemo.repository.QutoMongoBlockingRepositoryCustom;
//import com.tiket.tix.common.monitor.aspects.Monitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class QuoteBlockingController {

    private static final int DELAY_PER_ITEM_MS = 100;

    private static final Logger LOGGER = LoggerFactory.getLogger(QuoteBlockingController.class);

    @Autowired
    private QuoteMongoBlockingRepository quoteMongoBlockingRepository;

    @Autowired
    private QutoMongoBlockingRepositoryCustom qutoMongoBlockingRepositoryCustom;

    @Autowired
    private DemoProducer demoProducer;


    public QuoteBlockingController(final QuoteMongoBlockingRepository quoteMongoBlockingRepository) {
        this.quoteMongoBlockingRepository = quoteMongoBlockingRepository;
    }

    @GetMapping("/quotes-blocking")
    public Iterable<Quote> getQuotesBlocking() throws Exception {
        Thread.sleep(DELAY_PER_ITEM_MS * quoteMongoBlockingRepository.count());
        return quoteMongoBlockingRepository.findAll();
    }

    @GetMapping("/quotes-blocking-paged")
    public Iterable<Quote> getQuotesBlocking(final @RequestParam(name = "page") int page,
                                             final @RequestParam(name = "size") int size) throws Exception {
//        Thread.sleep(DELAY_PER_ITEM_MS * size);
//        throw new Exception("tmp");
        return quoteMongoBlockingRepository.findAllByIdNotNullOrderByIdAsc(PageRequest.of(page, size));
    }

    @GetMapping("/top-quotes")
    public Iterable<Quote> getTopQuotes() {
        LOGGER.info("Calling getTopQuotes......");
        return qutoMongoBlockingRepositoryCustom.findTop(12);
    }

    @GetMapping("/produceQuote")
    public Mono<Void> produceMessage() {
        return demoProducer.produce();
    }

}
