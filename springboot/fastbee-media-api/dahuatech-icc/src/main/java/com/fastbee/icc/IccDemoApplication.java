package com.fastbee.icc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(value = "com.fastbee.icc",lazyInit = true)
@EnableScheduling
public class IccDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(IccDemoApplication.class, args);
	}

}
