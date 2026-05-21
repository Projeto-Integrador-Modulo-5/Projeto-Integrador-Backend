package com.projeto.integrador.backend;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Teste de integração — requer infraestrutura completa (PostgreSQL, Redis, Kafka).
 * Desabilitado na suite de testes unitários. Execute com mvn verify -Pintegration
 * quando a infra estiver disponível.
 */
@Disabled("Teste de integração: requer PostgreSQL, Redis e Kafka rodando")
@SpringBootTest
class ApplicationTests {

	@Test
	void contextLoads() {
	}

}
