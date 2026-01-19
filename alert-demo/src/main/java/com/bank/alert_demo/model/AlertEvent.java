package com.bank.alert_demo.model;

import java.util.Map;

public record AlertEvent(
    String domain,
    String alertType, // Added missing field
    String eventId,
    String userId,
    String severity,
    String eventTs,
    String correlationId,
    Map<String, Object> payload
) {}
