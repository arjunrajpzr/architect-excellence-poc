package com.bank.alert_demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bank.alert_demo.entity.Alert;
import com.bank.alert_demo.repository.AlertRepository;
import com.bank.alert_demo.service.AlertService;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @Mock
    private AlertRepository alertRepository;

    @InjectMocks
    private AlertService alertService;

    @Test
    void getAllAlerts_ShouldReturnAll() {
        Alert alert1 = Alert.builder().eventId("1").build();
        Alert alert2 = Alert.builder().eventId("2").build();
        // when(alertRepository.findAll()).willReturn(Arrays.asList(alert1, alert2));

        List<Alert> result = alertService.getAllAlerts();

        assertEquals(2, result.size());
        verify(alertRepository).findAll();
    }

    @Test
    void getAlertById_ShouldReturnAlert() {
        String id = "123";
        Alert alert = Alert.builder().eventId(id).build();
        // when(alertRepository.findByEventId(id)).willReturn(Optional.of(alert));

        Optional<Alert> result = alertService.getAlertById(id);

        assertTrue(result.isPresent());
        // assertEquals(id, result.get().getEventId());
    }
}