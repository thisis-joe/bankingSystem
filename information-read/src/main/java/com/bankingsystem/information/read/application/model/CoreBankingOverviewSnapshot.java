package com.bankingsystem.information.read.application.model;

import java.util.List;

public record CoreBankingOverviewSnapshot(
    String pageTitle,
    String pageSubtitle,
    String generatedAt,
    int refreshIntervalSeconds,
    List<DashboardMetric> metrics,
    List<MonitoringStatusItem> statusItems,
    List<AccountIntegrityAlert> accountAlerts,
    List<TransactionTimelineItem> transactionTimeline,
    List<ApiTrafficItem> apiTraffic,
    List<BatchExecutionItem> batchExecutions,
    List<OperationalLogItem> operationalLogs,
    List<SystemFact> systemFacts,
    List<AdminActionItem> actions
) {
}
