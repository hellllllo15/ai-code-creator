package com.example.code;

import dev.langchain4j.community.store.embedding.redis.spring.RedisEmbeddingStoreAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {RedisEmbeddingStoreAutoConfiguration.class})
@MapperScan("com.example.code.mapper")
public class AiCodeCreatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiCodeCreatorApplication.class, args);
	}

}
