package com.bank.alert_demo.service;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import com.bank.alert_demo.DeliveryStatus;
import com.bank.alert_demo.entity.Alert;
import com.bank.alert_demo.model.AlertEvent;
import com.bank.alert_demo.repository.AlertRepository;
import com.rabbitmq.client.Channel;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AlertConsumer {

    @Autowired
    private AlertRepository alertRepository;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "inbound_alerts", ackMode = "MANUAL")
    @Transactional
    public void consumeAlert(AlertEvent event, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag){
        log.info("Received Event: ID={}, Type={}", event.eventId(), event.alertType());
        try {

            if (event.userId() == null || event.alertType() == null) {
                    throw new IllegalArgumentException("Missing critical fields: userId or alertType");
            }

            Alert alert = Alert.builder()
                        .domain(event.domain())
                        .alertType(event.alertType())
                        .eventId(event.eventId())
                        .userId(event.userId())
                        .severity(event.severity())
                        .eventTs(event.eventTs())
                        .correlationId(event.correlationId())
                        .payload(event.payload())
                        .deliveryStatus(DeliveryStatus.PENDING)
                        .createdAt(LocalDateTime.now().toString())
                        .build();

            alertRepository.save(alert);
            log.info("alert saved; alert={}", alert);
            rabbitTemplate.convertAndSend("notify_alerts", alert);

            alert.setDeliveryStatus(DeliveryStatus.DELIVERED);
            alertRepository.save(alert);
            channel.basicAck(tag, false);
        } catch (IOException | IllegalArgumentException | AmqpException e) {
            log.error("Error in processing event", e);
            try {
                channel.basicNack(tag, false, false);
            } catch (IOException ioException) {
                log.error("Failed to nack message", ioException);
            }
        }
    
    }
}
