package com.bankingsystem.channel.api.application.service;

import com.bankingsystem.information.read.application.model.AccountIntegrityAlert;
import com.bankingsystem.information.read.application.model.AdminActionItem;
import com.bankingsystem.information.read.application.model.ApiTrafficItem;
import com.bankingsystem.information.read.application.model.BatchExecutionItem;
import com.bankingsystem.information.read.application.model.CoreBankingOverviewSnapshot;
import com.bankingsystem.information.read.application.model.DashboardMetric;
import com.bankingsystem.information.read.application.model.MonitoringStatusItem;
import com.bankingsystem.information.read.application.model.OperationalLogItem;
import com.bankingsystem.information.read.application.model.SystemFact;
import com.bankingsystem.information.read.application.model.TransactionTimelineItem;
import com.bankingsystem.information.read.application.service.CoreBankingOverviewReadService;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class CoreBankingAdminScreenService {

    private final CoreBankingOverviewReadService coreBankingOverviewReadService;

    public CoreBankingAdminScreenService(CoreBankingOverviewReadService coreBankingOverviewReadService) {
        this.coreBankingOverviewReadService = coreBankingOverviewReadService;
    }

    public Map<String, Object> buildScreen() {
        CoreBankingOverviewSnapshot snapshot = coreBankingOverviewReadService.readOverview();

        Map<String, Object> screen = new LinkedHashMap<>();
        screen.put("screenId", "core-banking-overview");
        screen.put("title", snapshot.pageTitle());
        screen.put("subtitle", snapshot.pageSubtitle());
        screen.put("layout", "single-page-console");
        screen.put("generatedAt", snapshot.generatedAt());
        screen.put("refresh", Map.of(
            "enabled", true,
            "intervalSeconds", snapshot.refreshIntervalSeconds()
        ));
        screen.put("theme", Map.of(
            "name", "clean-admin",
            "accent", "#2563eb",
            "surface", "#f4f7fb",
            "panel", "#ffffff"
        ));
        screen.put("blocks", List.of(
            metricBlock(snapshot.metrics(), 12),
            statusBlock(snapshot.statusItems(), 12),
            tableBlock(
                "integrity-alerts",
                "정합성 경고",
                "원장과 잔액, 자동이체, 멱등 처리에서 운영자가 먼저 확인해야 하는 경고 목록",
                List.of("심각도", "계좌번호", "이슈", "감지시각", "조치 가이드"),
                snapshot.accountAlerts().stream().map(this::toAlertRow).toList(),
                7
            ),
            tableBlock(
                "batch-executions",
                "배치 실행 현황",
                "정기 작업이 언제 실행되었고 다음 실행이 언제인지 빠르게 확인하는 영역",
                List.of("배치명", "상태", "마지막 실행", "다음 실행", "메모"),
                snapshot.batchExecutions().stream().map(this::toBatchRow).toList(),
                5
            ),
            tableBlock(
                "api-traffic",
                "최근 채널 요청",
                "요청 식별자, 응답 코드, 지연시간을 함께 확인하는 채널계 흐름 요약",
                List.of("클라이언트", "요청 ID", "응답", "Trace ID", "요청시각", "지연시간"),
                snapshot.apiTraffic().stream().map(this::toApiTrafficRow).toList(),
                7
            ),
            logBlock(snapshot.operationalLogs(), 5),
            timelineBlock(snapshot.transactionTimeline(), 6),
            factsBlock(snapshot.systemFacts(), 6),
            actionsBlock(snapshot.actions(), 12)
        ));
        return screen;
    }

    private Map<String, Object> metricBlock(List<DashboardMetric> metrics, int span) {
        return block(
            "metrics",
            "metric-grid",
            "핵심 운영 지표",
            "지금 상태를 가장 빨리 파악하기 위한 상단 요약",
            span,
            Map.of("items", metrics.stream().map(this::toMetricItem).toList())
        );
    }

    private Map<String, Object> statusBlock(List<MonitoringStatusItem> statusItems, int span) {
        return block(
            "status-grid",
            "status-grid",
            "시스템 상태 요약",
            "계정계, 채널계, 배치, 정보계 상태를 한 줄에서 빠르게 읽기 위한 상태 카드",
            span,
            Map.of("items", statusItems.stream().map(this::toStatusItem).toList())
        );
    }

    private Map<String, Object> timelineBlock(List<TransactionTimelineItem> items, int span) {
        return block(
            "transaction-timeline",
            "timeline",
            "최근 거래 흐름",
            "최근 입금, 출금, 이체가 어떤 상태로 처리되었는지 시간순으로 확인",
            span,
            Map.of("items", items.stream().map(this::toTimelineItem).toList())
        );
    }

    private Map<String, Object> factsBlock(List<SystemFact> facts, int span) {
        return block(
            "system-facts",
            "fact-list",
            "운영 메모",
            "현재 화면이 어떤 구조와 갱신 방식으로 동작하는지 정리한 참고 정보",
            span,
            Map.of("items", facts.stream().map(fact -> Map.of(
                "label", fact.label(),
                "value", fact.value()
            )).toList())
        );
    }

    private Map<String, Object> actionsBlock(List<AdminActionItem> actions, int span) {
        return block(
            "actions",
            "action-list",
            "운영 액션",
            "수동 새로고침, 자동 새로고침 제어, 참고 링크 이동을 모아 둔 영역",
            span,
            Map.of("items", actions.stream().map(action -> Map.of(
                "label", action.label(),
                "tone", action.tone(),
                "actionType", action.actionType(),
                "target", action.target(),
                "description", action.description()
            )).collect(Collectors.toList()))
        );
    }

    private Map<String, Object> tableBlock(
        String blockId,
        String title,
        String description,
        List<String> columns,
        List<List<String>> rows,
        int span
    ) {
        return block(
            blockId,
            "table",
            title,
            description,
            span,
            Map.of(
                "columns", columns,
                "rows", rows
            )
        );
    }

    private Map<String, Object> logBlock(List<OperationalLogItem> logs, int span) {
        return block(
            "operational-logs",
            "log-list",
            "운영 로그",
            "최근 운영 이벤트를 레벨, 분류, Trace ID 기준으로 빠르게 읽는 영역",
            span,
            Map.of("items", logs.stream().map(this::toLogItem).toList())
        );
    }

    private Map<String, Object> block(
        String blockId,
        String component,
        String title,
        String description,
        int span,
        Map<String, Object> payload
    ) {
        Map<String, Object> block = new LinkedHashMap<>();
        block.put("blockId", blockId);
        block.put("component", component);
        block.put("title", title);
        block.put("description", description);
        block.put("span", span);
        block.putAll(payload);
        return block;
    }

    private Map<String, Object> toMetricItem(DashboardMetric metric) {
        return Map.of(
            "key", metric.key(),
            "label", metric.label(),
            "value", metric.value(),
            "emphasis", metric.emphasis(),
            "description", metric.description()
        );
    }

    private Map<String, Object> toStatusItem(MonitoringStatusItem item) {
        return Map.of(
            "label", item.label(),
            "state", item.state(),
            "summary", item.summary(),
            "detail", item.detail()
        );
    }

    private List<String> toAlertRow(AccountIntegrityAlert alert) {
        return List.of(
            alert.severity(),
            alert.accountNo(),
            alert.issue(),
            alert.detectedAt(),
            alert.actionGuide()
        );
    }

    private List<String> toBatchRow(BatchExecutionItem item) {
        return List.of(
            item.jobName(),
            item.status(),
            item.lastRunAt(),
            item.nextRunAt(),
            item.note()
        );
    }

    private Map<String, Object> toTimelineItem(TransactionTimelineItem item) {
        return Map.of(
            "eyebrow", item.transactionType(),
            "title", item.summary(),
            "meta", item.transactionId(),
            "timestamp", item.occurredAt(),
            "status", item.status()
        );
    }

    private List<String> toApiTrafficRow(ApiTrafficItem item) {
        return List.of(
            item.clientName(),
            item.requestId(),
            item.responseStatus(),
            item.traceId(),
            item.requestedAt(),
            item.latency()
        );
    }

    private Map<String, Object> toLogItem(OperationalLogItem item) {
        return Map.of(
            "level", item.level(),
            "category", item.category(),
            "message", item.message(),
            "occurredAt", item.occurredAt(),
            "traceId", item.traceId()
        );
    }
}
