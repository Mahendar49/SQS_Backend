package com.smartqueue.queue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class QueueServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(QueueServiceApplication.class, args);
	}

}
