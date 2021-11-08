package com.example.reactiveDemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.tiket.tix", "com.example.reactiveDemo"})
public class ReactiveDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReactiveDemoApplication.class, args);

//		ConfigurableApplicationContext context = SpringApplication.run(ReactiveDemoApplication.class, args);
//		Object o = context.getBeanFactory().getBean(CustomFilter.java.class);

	}

}