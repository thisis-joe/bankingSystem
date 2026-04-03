# 핵심 테이블 DDL 초안

## 1. 이번 문서의 목적

이 문서는 코어 뱅킹 아키텍처 문서를 바탕으로 실제 구현에 바로 사용할 수 있는 1차 DDL 기준을 정리한 문서다.
이번 단계에서는 전체 시스템 모든 테이블을 한 번에 만들지 않고, 계정계의 핵심 흐름을 먼저 구성하는 데 필요한 테이블을 우선 정의한다.

이번 문서와 함께 실제 SQL 파일도 추가한다.

- 설명 문서: `docs/핵심-테이블-DDL-초안.md`
- 실제 SQL 파일: `database/ddl/V1__core_banking_core_schema.sql`

## 2. 왜 DDL 초안이 먼저 필요한가

아키텍처 문서만 있으면 방향은 이해할 수 있지만, 실제 개발은 테이블 구조가 정해져야 다음 단계로 진행할 수 있다.
특히 Spring Boot에서는 다음 작업들이 모두 테이블 구조에 의존한다.

- JPA 엔티티 설계
- Repository 작성
- 서비스 트랜잭션 흐름 작성
- 테스트 데이터 생성
- API 요청과 응답 구조 설계

즉, DDL 초안은 단순한 DB 문서가 아니라 이후 백엔드 전체 구조의 기준점 역할을 한다.

## 3. 이번 단계에서 포함한 테이블 범위

이번 1차 DDL에는 아래 범위를 포함했다.

### 3.1 기준 데이터

- `customer`
- `product`
- `account`
- `account_holder`

### 3.2 현재 상태 데이터

- `account_balance`

### 3.3 거래 및 원장 이력 데이터

- `bank_transaction`
- `transaction_entry`
- `account_ledger`
- `account_status_history`

### 3.4 자동이체 데이터

- `auto_transfer_rule`
- `auto_transfer_schedule`
- `auto_transfer_execution`

### 3.5 이자 데이터

- `interest_policy`
- `interest_calculation_batch`
- `interest_accrual_history`
- `interest_posting_history`

### 3.6 채널 및 추적 데이터

- `channel`
- `api_client`
- `api_request_log`
- `authentication_audit_log`
- `outbox_event`

## 4. 이번 DDL의 설계 기준

### 4.1 DB 가정

이번 SQL 초안은 `PostgreSQL` 기준으로 작성했다.
이유는 다음과 같다.

- Spring Boot와 연결 사례가 많다.
- 트랜잭션, 락, 인덱스, 제약조건 기능이 안정적이다.
- 추후 JSON, 이벤트 저장, 조회 최적화에도 확장성이 좋다.

나중에 Oracle이나 다른 DB로 바꾸더라도, 현재 단계에서는 테이블 구조와 관계를 확정하는 것이 더 중요하다.

### 4.2 데이터 타입 기준

- 식별자는 `bigserial` 또는 `bigint`
- 금액은 `numeric(18, 2)`
- 비율이나 금리는 `numeric(9, 6)`
- 시각은 `timestamptz`
- 일자는 `date`
- 상태값과 유형값은 우선 문자열 `varchar` 로 두고, 추후 코드 테이블 또는 enum 전략을 선택한다.

### 4.3 이번 단계의 핵심 원칙

- 현재 잔액과 원장을 분리한다.
- 거래 헤더와 거래 엔트리를 분리한다.
- 자동이체와 이자 계산도 일반 거래와 연결 가능하게 만든다.
- API 요청 추적과 멱등 처리 기반을 테이블 차원에서 준비한다.
- 정보계 반영을 위한 아웃박스 이벤트 구조를 함께 둔다.

## 5. 테이블별 핵심 해설

### 5.1 `account` 와 `account_balance` 를 나눈 이유

`account` 는 계좌 자체의 기준 데이터다.
반면 `account_balance` 는 계좌의 현재 상태다.

이 둘을 분리하는 이유는 다음과 같다.

- 계좌 자체의 생명주기와 잔액은 성격이 다르다.
- 잔액은 거래가 발생할 때마다 자주 변경된다.
- 잔액에 락이나 버전 관리를 적용할 때 기준 데이터와 분리하는 편이 안정적이다.

### 5.2 `bank_transaction` 과 `transaction_entry` 를 나눈 이유

`bank_transaction` 은 하나의 업무 거래를 나타낸다.
`transaction_entry` 는 그 거래가 실제로 어떤 계좌에 어떤 금액 영향을 주었는지를 나타낸다.

예를 들어 계좌이체 하나는 업무적으로는 한 건이지만, 실제 반영은 다음 두 엔트리로 나뉜다.

- 출금 계좌 엔트리
- 입금 계좌 엔트리

이 구조를 사용하면 거래의 업무 의미와 실제 금전 반영을 분리해서 관리할 수 있다.

### 5.3 `account_ledger` 가 필요한 이유

`transaction_entry` 만으로도 자금 반영을 표현할 수는 있지만, 계좌 중심 조회와 감사 추적을 쉽게 하려면 계좌별 원장 흐름이 필요하다.
그래서 `account_ledger` 를 별도로 두어 계좌 단위 시간 흐름을 빠르게 따라갈 수 있게 한다.

### 5.4 `outbox_event` 를 지금부터 두는 이유

초기 단계부터 계정계와 정보계를 분리하려면, 거래 반영 후 안전하게 이벤트를 전달할 구조가 필요하다.
`outbox_event` 는 바로 그 역할을 한다.

이 테이블이 있으면 다음이 쉬워진다.

- 거래 반영과 이벤트 기록을 같은 트랜잭션으로 묶기
- 정보계 조회 모델 갱신
- 추후 Kafka 같은 메시지 브로커 연동

## 6. 인덱스 설계에서 중요하게 본 점

이번 DDL에서는 다음 인덱스를 중요하게 반영했다.

- 계좌번호, 고객번호, 상품코드 같은 업무 식별값 유니크 보장
- 거래 조회를 위한 발생 시각 인덱스
- 계좌별 거래내역 조회를 위한 계좌 식별자 + 시각 인덱스
- 자동이체 스케줄 조회를 위한 다음 실행 시각 인덱스
- 요청 추적을 위한 `request_id`, `idempotency_key`, `trace_id` 인덱스

## 7. 이후 바로 이어질 작업

이번 DDL 초안 다음 단계로는 아래 순서가 자연스럽다.

1. Spring Boot 멀티모듈 뼈대 생성
2. 공통 감사 필드와 금액 타입 정의
3. `customer`, `account`, `account_balance` 엔티티 작성
4. `bank_transaction`, `transaction_entry`, `account_ledger` 엔티티 작성
5. 입금, 출금, 이체 서비스 스켈레톤 작성

## 8. 학습 포인트

- 기준 데이터와 현재 상태 데이터를 왜 분리하는지
- 원장과 잔액을 동시에 관리하는 이유
- 업무 거래와 실제 금전 반영을 왜 분리하는지
- PK, FK, 유니크 제약, 인덱스가 각각 어떤 역할을 하는지
- Spring Boot 엔티티 설계가 왜 DDL 설계와 거의 같이 가야 하는지
