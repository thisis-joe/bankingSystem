package com.bankingsystem.information.read.application.model;

public record BatchExecutionItem(
    String jobName,
    String status,
    String lastRunAt,
    String nextRunAt,
    String note
) {
}
