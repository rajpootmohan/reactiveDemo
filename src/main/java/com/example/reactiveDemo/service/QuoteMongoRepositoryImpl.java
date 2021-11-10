package com.example.reactiveDemo.service;

import com.example.reactiveDemo.controller.QuoteBlockingController;
import com.example.reactiveDemo.model.Quote;
import com.example.reactiveDemo.repository.QutoMongoBlockingRepositoryCustom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class QuoteMongoRepositoryImpl implements QutoMongoBlockingRepositoryCustom {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuoteBlockingController.class);

//    @Autowired
//    private ReactiveMongoTemplate reactiveMongoTemplate;

    @Override
    public List<Quote> findTop() {
        LOGGER.info("Calling findTop in repository......");
        List<Quote> quotes = new ArrayList<>();
        quotes.add(new Quote("1", "first", "first book 1"));
        return quotes;
    }
}
