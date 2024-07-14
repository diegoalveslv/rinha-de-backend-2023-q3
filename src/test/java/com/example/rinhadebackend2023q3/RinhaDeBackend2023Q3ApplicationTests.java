package com.example.rinhadebackend2023q3;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest

@Import({TestcontainersConfigurationPostgres.class, TestcontainersConfigurationRedis.class})
class RinhaDeBackend2023Q3ApplicationTests {

	@Test
	void contextLoads() {
	}

}
