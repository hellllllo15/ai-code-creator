package com.example.code;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@MapperScan("com.example.code.mapper")
class AiCodeCreatorApplicationTests {

	@Test
	void contextLoads() {
	}

}
