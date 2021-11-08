package com.example.reactiveDemo.repository;

import com.example.reactiveDemo.model.Quote;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QutoMongoBlockingRepositoryCustom {

    List<Quote> findTop(int a);
}
