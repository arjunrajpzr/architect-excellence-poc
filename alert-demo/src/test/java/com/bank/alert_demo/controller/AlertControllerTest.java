package com.bank.alert_demo.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.bank.alert_demo.entity.Alert;
import com.bank.alert_demo.service.AlertService;

@ExtendWith(MockitoExtension.class)
class AlertControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AlertService alertService;

    @InjectMocks
    private AlertController alertController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(alertController).build();
    }

    @Test
    void getAllAlerts_ShouldReturnList() throws Exception {
        given(alertService.getAllAlerts()).willReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/alerts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAlertById_WhenFound_ShouldReturnAlert() throws Exception {
        String alertId = "123";
        Alert alert = Alert.builder()
                .eventId(alertId)
                .alertType("INFO")
                .build();
        
        given(alertService.getAlertById(alertId)).willReturn(Optional.of(alert));

        mockMvc.perform(get("/api/v1/alerts/{id}", alertId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAlertById_WhenNotFound_ShouldReturn404() throws Exception {
        String alertId = "999";
        given(alertService.getAlertById(alertId)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/alerts/{id}", alertId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}