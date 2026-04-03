package com.bankingsystem.information.read.application.model;

public record MonitoringStatusItem(
    String label,
    String state,
    String summary,
    String detail
) {
}
