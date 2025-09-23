package com.example.code;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.code.mapper")
public class AiCodeCreatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiCodeCreatorApplication.class, args);
	}

}
