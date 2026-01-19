package com.bank.alert_demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bank.alert_demo.entity.Alert;
import com.bank.alert_demo.repository.AlertRepository;

@Service
public class AlertService {

    @Autowired
    private AlertRepository alertRepository;

    public List<Alert> getAllAlerts() {
        return alertRepository.findAll();
    }

    public Optional<Alert> getAlertById(String id) {
        return alertRepository.findByEventId(id);
    }

}
