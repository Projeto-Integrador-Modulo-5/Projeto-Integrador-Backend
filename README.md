# Projeto-Integrador-Backend

Microsserviço de API REST principal do e-commerce de camisetas — Projeto Integrador ADS 4º Período · PUC Goiás · 2026/1.

Responsável por autenticação de usuários, CRUD de produtos, criação de pedidos e publicação de eventos no Apache Kafka.

---

## Responsabilidades

- Autenticação e autorização com JWT (`CUSTOMER` e `ADMIN`)
- CRUD do catálogo de produtos (camisetas)
- Recebimento e persistência de pedidos via REST
- Publicação de `OrderCreatedEvent` no tópico `orders-topic` do Kafka
- Integração com gateway de pagamento (REST externo)

---

## Stack

| Camada       | Tecnologia                              |
|--------------|-----------------------------------------|
| Linguagem    | Java 21                                 |
| Framework    | Spring Boot 3.x (Web, Data JPA, Security, Kafka) |
| Banco        | PostgreSQL 15                           |
| Cache        | Redis 7 (sessões de usuário)            |
| Mensageria   | Apache Kafka — produtor `orders-topic`  |
| Build        | Maven (Wrapper incluído)                |
| Container    | Docker (orquestrado via `Projeto-Integrador-Infra`) |

---

## Estrutura de pacotes

```
com.ecommerce.backend/
├── controller/   # @RestController + DTOs de entrada/saída
├── service/      # @Service + lógica de negócio
├── repository/   # JpaRepository — acesso a dados
├── messaging/    # KafkaTemplate — produtor de eventos
├── domain/       # Entidades, Sealed Interface, Factory — zero framework
└── dto/          # Java Records (Request / Response / Event)
```

> O pacote `domain/` não importa nenhuma classe do Spring Framework — regra da Clean Architecture.

---

## Design Patterns aplicados

| Pattern    | Onde                  | Justificativa                                              |
|------------|-----------------------|------------------------------------------------------------|
| Factory    | `OrderFactory`        | Centraliza a construção do agregado `Order`                |
| Repository | `JpaRepository`       | Isola o acesso a dados da lógica de negócio                |
| Strategy   | `PricingStrategy`     | Encapsula regras de precificação sem alterar código existente |
| Observer   | Kafka Topics          | Desacoplamento total via eventos                           |
| DI         | Injeção por construtor | Inversão de controle, facilita mocking                    |

---

## Features Java 21 utilizadas

- **Virtual Threads (Project Loom)** — `spring.threads.virtual.enabled=true`
- **Records** — DTOs imutáveis (`OrderRequest`, `OrderResponse`, `OrderEvent`)
- **Sealed Interfaces** — `OrderStatus` com estados fechados
- **Pattern Matching** — `switch(status)` sem casting manual

---

## Configuração

```bash
cp .env.example .env
# configure DATABASE_URL, KAFKA_BOOTSTRAP_SERVERS, JWT_SECRET, REDIS_URL
```

---

## Executando localmente

> Recomendado subir a infraestrutura pelo repositório `Projeto-Integrador-Infra` antes.

```bash
./mvnw spring-boot:run
```

A aplicação sobe na porta `8080`.

---

## Testes

```bash
# Unitários + cobertura JaCoCo
./mvnw verify

# Relatório de cobertura
open target/site/jacoco/index.html
```

Cobertura mínima exigida: **70%** na camada `service/`.

---

## Endpoints principais

| Método | Rota                   | Descrição                         | Auth         |
|--------|------------------------|-----------------------------------|--------------|
| POST   | `/api/auth/login`      | Autenticação, retorna JWT         | Público      |
| GET    | `/api/products`        | Lista produtos disponíveis        | Público      |
| POST   | `/api/products`        | Cria produto                      | `ADMIN`      |
| POST   | `/api/orders`          | Cria pedido e publica no Kafka    | `CUSTOMER`   |
| GET    | `/api/orders/{id}`     | Consulta status do pedido         | `CUSTOMER`   |

---

## Repositórios relacionados

| Repositório | Responsabilidade |
|---|---|
| [Projeto-Integrador-Infra](https://github.com/Projeto-Integrador-Modulo-5/Projeto-Integrador-Infra) | Docker Compose e infraestrutura |
| [Projeto-Integrador-Logistics-Service](https://github.com/Projeto-Integrador-Modulo-5/Projeto-Integrador-Logistics-Service) | Consome `orders-topic` e processa pedidos |
| [Projeto-Integrador-Notification-Service](https://github.com/Projeto-Integrador-Modulo-5/Projeto-Integrador-Notification-Service) | Notifica clientes via WebSocket e e-mail |
| [Projeto-Integrador-Frontend](https://github.com/Projeto-Integrador-Modulo-5/Projeto-Integrador-Frontend) | Interface web do cliente e administrador |
