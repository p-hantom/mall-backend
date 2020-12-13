package com.mall.mallbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class MallBackendApplication {

	public static void main(String[] args) {
		System.out.println("main");
		SpringApplication.run(MallBackendApplication.class, args);
	}

}
