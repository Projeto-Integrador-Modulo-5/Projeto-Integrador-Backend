package com.projeto.integrador.backend.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventProducer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventProducer.class);
    private static final String TOPIC_ORDER_CREATED = "order.created";
    private static final String TOPIC_NOTIFICATION = "notification.send";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public OrderEventProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishOrderCreated(OrderCreatedEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            log.info("Publicando order.created: orderId={}", event.orderId());
            kafkaTemplate.send(TOPIC_ORDER_CREATED, event.orderId().toString(), json);
        } catch (JsonProcessingException e) {
            log.error("Erro ao serializar OrderCreatedEvent", e);
            throw new RuntimeException("Erro ao publicar evento de pedido", e);
        }
    }

    public void publishNotification(NotificationEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            log.info("Publicando notification.send: userId={}, type={}", event.userId(), event.type());
            kafkaTemplate.send(TOPIC_NOTIFICATION, event.userId().toString(), json);
        } catch (JsonProcessingException e) {
            log.error("Erro ao serializar NotificationEvent", e);
            throw new RuntimeException("Erro ao publicar notificação", e);
        }
    }
}
