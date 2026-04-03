package com.bankingsystem.information.read.application.model;

public record AdminActionItem(
    String label,
    String tone,
    String actionType,
    String target,
    String description
) {
}
