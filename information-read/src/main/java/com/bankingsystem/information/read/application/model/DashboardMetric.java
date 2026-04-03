package com.bankingsystem.information.read.application.model;

public record DashboardMetric(
    String key,
    String label,
    String value,
    String emphasis,
    String description
) {
}
