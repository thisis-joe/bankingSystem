package com.bankingsystem.information.read.application.service;

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
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CoreBankingOverviewReadService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    public CoreBankingOverviewSnapshot readOverview() {
        OffsetDateTime now = OffsetDateTime.now();

        return new CoreBankingOverviewSnapshot(
            "코어 뱅킹 운영 콘솔",
            "원장, 채널, 배치, 로그를 한 페이지에서 구조적으로 확인하는 SDUI 관리자 화면",
            format(now),
            10,
            List.of(
                new DashboardMetric("accounts", "활성 계좌", "128,402", "stable", "현재 기준 활성 상태 계좌 수"),
                new DashboardMetric("ledger", "당일 원장 반영", "2,381", "focus", "오늘 반영 완료된 거래 엔트리 수"),
                new DashboardMetric("api", "최근 5분 API 성공률", "99.94%", "stable", "채널계 오픈 API 성공 응답 비율"),
                new DashboardMetric("alerts", "즉시 점검 경고", "3", "danger", "정합성, 배치, 채널 추적 경고 수")
            ),
            List.of(
                new MonitoringStatusItem("계정계 원장", "정상", "반영 지연 없음", "마지막 정합성 검증 8초 전 완료"),
                new MonitoringStatusItem("채널 API", "주의", "409 응답 1건", "멱등키 재사용 요청이 최근 5분 내 1건 발생"),
                new MonitoringStatusItem("자동이체 배치", "주의", "1건 지연", "예약 실행 대기열에 1건이 지연 상태로 남아 있음"),
                new MonitoringStatusItem("정보계 읽기 모델", "정상", "지연 2초 미만", "운영 대시보드 스냅샷 최신화 정상"),
                new MonitoringStatusItem("보안 설정", "정상", "학습용 허용 정책", "다음 단계에서 API 클라이언트 인증 필터를 연결 예정"),
                new MonitoringStatusItem("DB 연결", "정상", "H2 메모리 실행 중", "로컬 학습용 런타임 데이터베이스")
            ),
            List.of(
                new AccountIntegrityAlert("높음", "110-234-000981", "원장 합계와 가용 잔액 차이 감지", format(now.minusMinutes(7)), "거래 982341 재처리 여부 확인"),
                new AccountIntegrityAlert("중간", "110-234-000127", "자동이체 예약 실행 지연", format(now.minusMinutes(14)), "배치 로그와 락 점유 상태 확인"),
                new AccountIntegrityAlert("낮음", "110-234-000412", "최근 API 재시도 3회 발생", format(now.minusMinutes(21)), "멱등키 재사용 패턴 점검")
            ),
            List.of(
                new TransactionTimelineItem("TRANSFER", "TX-20260403-000231", "급여 계좌 이체 8,250,000원", format(now.minusSeconds(40)), "POSTED"),
                new TransactionTimelineItem("DEPOSIT", "TX-20260403-000228", "법인 수납 입금 12,500,000원", format(now.minusMinutes(3)), "POSTED"),
                new TransactionTimelineItem("WITHDRAWAL", "TX-20260403-000223", "대량 출금 요청 차단", format(now.minusMinutes(11)), "REJECTED"),
                new TransactionTimelineItem("TRANSFER", "TX-20260403-000216", "자동이체 실행 보류", format(now.minusMinutes(18)), "PENDING")
            ),
            List.of(
                new ApiTrafficItem("기본 오픈 API 클라이언트", "REQ-20260403-1001", "200", "trace-01", format(now.minusSeconds(28)), "31ms"),
                new ApiTrafficItem("기업 자금 파트너", "REQ-20260403-1000", "409", "trace-02", format(now.minusSeconds(42)), "18ms"),
                new ApiTrafficItem("기본 오픈 API 클라이언트", "REQ-20260403-0997", "200", "trace-03", format(now.minusMinutes(1)), "24ms"),
                new ApiTrafficItem("정산 배치 호출기", "REQ-20260403-0995", "200", "trace-04", format(now.minusMinutes(2)), "55ms"),
                new ApiTrafficItem("기업 자금 파트너", "REQ-20260403-0991", "400", "trace-05", format(now.minusMinutes(4)), "12ms")
            ),
            List.of(
                new BatchExecutionItem("interest-accrual-daily", "정상", format(now.minusMinutes(15)), format(now.plusHours(7)), "이자 계산 배치가 예정 주기 내 정상 완료"),
                new BatchExecutionItem("auto-transfer-dispatch", "주의", format(now.minusMinutes(6)), format(now.plusMinutes(4)), "1건 지연, 수동 재실행 검토 필요"),
                new BatchExecutionItem("ledger-reconciliation", "정상", format(now.minusMinutes(12)), format(now.plusMinutes(18)), "원장/잔액 대사 배치 정상"),
                new BatchExecutionItem("api-log-compaction", "정상", format(now.minusMinutes(30)), format(now.plusHours(1)), "요청 로그 요약 적재 완료")
            ),
            List.of(
                new OperationalLogItem("WARN", "멱등 처리", "같은 Idempotency-Key 가 다른 거래 유형에 재사용되어 차단됨", format(now.minusSeconds(42)), "trace-02"),
                new OperationalLogItem("INFO", "원장 반영", "거래 TX-20260403-000231 원장 및 잔액 반영 완료", format(now.minusSeconds(40)), "trace-01"),
                new OperationalLogItem("WARN", "자동이체", "예약 실행 시간이 지난 작업 1건이 대기열에 남아 있음", format(now.minusMinutes(6)), "trace-batch-01"),
                new OperationalLogItem("INFO", "채널 요청", "API 요청 로그 저장 완료, 응답 코드 200", format(now.minusMinutes(1)), "trace-03"),
                new OperationalLogItem("ERROR", "입력 검증", "기업 자금 파트너 요청에서 필수 헤더 누락으로 400 반환", format(now.minusMinutes(4)), "trace-05")
            ),
            List.of(
                new SystemFact("화면 렌더링 방식", "SDUI JSON + 범용 브라우저 렌더러"),
                new SystemFact("갱신 방식", "10초 주기 자동 새로고침"),
                new SystemFact("실시간 수준", "주기적 폴링 스냅샷"),
                new SystemFact("화면 정의 API", "/api/v1/admin/screens/core-banking-overview"),
                new SystemFact("정적 페이지", "/sdui-admin/index.html")
            ),
            List.of(
                new AdminActionItem("지금 새로고침", "primary", "refresh-screen", "/api/v1/admin/screens/core-banking-overview", "서버가 내려주는 최신 화면 정의를 즉시 다시 읽는다."),
                new AdminActionItem("자동 새로고침 일시정지", "neutral", "toggle-auto-refresh", "#", "주기적 새로고침을 잠시 멈추거나 다시 시작한다."),
                new AdminActionItem("SDUI 참고 글 열기", "neutral", "open-link", "https://blog.selectfromuser.com/server-driven-ui-for-admin/", "서버 주도 UI 개념을 설명하는 참고 글을 새 탭에서 연다.")
            )
        );
    }

    private String format(OffsetDateTime value) {
        return value.format(FORMATTER);
    }
}
