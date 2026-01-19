package com.bank.alert_demo.repository;


import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bank.alert_demo.entity.Alert;

public interface AlertRepository<EventId> extends JpaRepository<Alert, UUID>{
    Optional<Alert> findByEventId(String eventId);

}
