package com.bankingsystem.information.read.application.model;

public record AccountIntegrityAlert(
    String severity,
    String accountNo,
    String issue,
    String detectedAt,
    String actionGuide
) {
}
