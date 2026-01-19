package com.bank.alert_demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.alert_demo.entity.Alert;
import com.bank.alert_demo.service.AlertService;


@RestController
@RequestMapping("/api/v1/alerts")
public class AlertController {
 
    @Autowired
   private AlertService alertService;

   @GetMapping
    public List<Alert> getAllAlerts() {
        return alertService.getAllAlerts();
    }

    @GetMapping("/{alertId}")
    public ResponseEntity<Alert> getAlertById(@PathVariable String alertId) {
        return alertService.getAlertById(alertId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    

}
