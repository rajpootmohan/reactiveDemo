package com.example.reactiveDemo.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ConsumerRunner implements ApplicationRunner {

  @Autowired
  private DemoConsumer demoConsumer;

  @Value("1")
  private int concurrency;

  @Override
  public void run(ApplicationArguments arg0) {
    for (int i = 0; i < concurrency; i++) {
      executeConcurrent(demoConsumer.getRunnable());
    }
  }

  private void executeConcurrent(Runnable runnable) {
    ExecutorService executorService = Executors.newFixedThreadPool(concurrency);
    for (int i = 0; i < concurrency; i++) {
      executorService.execute(runnable);
    }
  }
}
