package com.bank.alert_demo.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.bank.alert_demo.entity.Alert;
import com.bank.alert_demo.model.AlertEvent;
import com.bank.alert_demo.repository.AlertRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;

@ExtendWith(MockitoExtension.class)
class AlertConsumerTest {

    @Mock
    private AlertRepository alertRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AlertConsumer alertConsumer;

    @Mock
    private Channel channel;

    @Mock
    private Message message;

    @Test
    void consumeAlert_Success() throws Exception {
        long tag = 123L;
        byte[] body = "{\"eventId\":\"1\"}".getBytes();
        when(message.getBody()).thenReturn(body);

        AlertEvent event = mock(AlertEvent.class);
        when(objectMapper.readValue(body, AlertEvent.class)).thenReturn(event);
        
        when(event.eventId()).thenReturn("evt-1");
        when(event.userId()).thenReturn("user-1");
        when(event.alertType()).thenReturn("CRITICAL");
        when(event.domain()).thenReturn("PAYMENT");

        alertConsumer.consumeAlert(message, channel, tag);

        // Should save twice: once PENDING, once DELIVERED
        verify(alertRepository, times(2)).save(any(Alert.class));
        verify(rabbitTemplate).convertAndSend(eq("notify_alerts"), any(Alert.class));
        verify(channel).basicAck(tag, false);
    }

    @Test
    void consumeAlert_JsonProcessingException() throws Exception {
        long tag = 123L;
        byte[] body = "invalid-json".getBytes();
        when(message.getBody()).thenReturn(body);

        when(objectMapper.readValue(body, AlertEvent.class)).thenThrow(new IOException("Bad JSON"));

        alertConsumer.consumeAlert(message, channel, tag);

        verify(alertRepository, never()).save(any());
        verify(channel).basicReject(tag, false);
    }

    @Test
    void consumeAlert_MissingCriticalFields() throws Exception {
        long tag = 123L;
        byte[] body = "{}".getBytes();
        when(message.getBody()).thenReturn(body);

        AlertEvent event = mock(AlertEvent.class);
        when(objectMapper.readValue(body, AlertEvent.class)).thenReturn(event);
        
        // Missing userId and alertType
        when(event.userId()).thenReturn(null);

        alertConsumer.consumeAlert(message, channel, tag);

        verify(alertRepository, never()).save(any());
        verify(channel).basicNack(tag, false, false);
    }

    @Test
    void consumeAlert_ProcessingException() throws Exception {
        long tag = 123L;
        byte[] body = "{}".getBytes();
        when(message.getBody()).thenReturn(body);

        AlertEvent event = mock(AlertEvent.class);
        when(objectMapper.readValue(body, AlertEvent.class)).thenReturn(event);
        when(event.userId()).thenReturn("user-1");
        when(event.alertType()).thenReturn("INFO");

        doThrow(new RuntimeException("DB Error")).when(alertRepository).save(any(Alert.class));

        alertConsumer.consumeAlert(message, channel, tag);

        verify(channel).basicNack(tag, false, false);
    }
}