package com.example.reactiveDemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


@SpringBootApplication( scanBasePackages = {"com.example.reactiveDemo", "com.tiket.tix"})
@EnableMongoRepositories
public class ReactiveDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReactiveDemoApplication.class, args);

//		ConfigurableApplicationContext context = SpringApplication.run(ReactiveDemoApplication.class, args);
//		Object o = context.getBeanFactory().getBean(CustomFilter.java.class);

	}

}