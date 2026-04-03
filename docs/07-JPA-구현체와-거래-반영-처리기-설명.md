# JPA 구현체와 거래 반영 처리기 설명

## 1. 이번 작업의 목적

이번 작업의 목적은 앞에서 만든 저장소 인터페이스와 이체 서비스 스켈레톤을 실제 저장과 반영 로직으로 연결하는 것이다.

이전 단계까지는 아래 상태였다.

- 엔티티 있음
- 저장소 인터페이스 있음
- 서비스 흐름 있음
- 하지만 실제 DB 저장 구현은 없음
- 실제 엔트리, 원장, 잔액 반영 구현도 없음

이번 작업에서는 바로 그 빈 부분을 채웠다.

## 2. 왜 JPA 구현체가 필요한가

도메인 계층의 저장소 인터페이스는 "무엇을 할 것인가"를 정의하는 계약이다.
하지만 실제 애플리케이션이 동작하려면 "어떻게 저장할 것인가"가 필요하다.

그래서 이번에는 인프라 계층에 Spring Data JPA 구현체를 만들었다.

구조는 두 단계로 나누었다.

1. Spring Data JPA 인터페이스
2. 도메인 저장소 인터페이스를 구현하는 어댑터 클래스

이렇게 나누는 이유는 도메인 계층이 Spring Data 기술에 직접 묶이지 않도록 하기 위해서다.

## 3. 계좌계 JPA 구현체 구조

계좌계에서는 아래 구성을 만들었다.

- `SpringDataCustomerJpaRepository`
- `SpringDataProductJpaRepository`
- `SpringDataAccountJpaRepository`
- `SpringDataAccountBalanceJpaRepository`

그리고 이들을 감싸는 어댑터 클래스를 만들었다.

- `CustomerRepositoryImpl`
- `ProductRepositoryImpl`
- `AccountRepositoryImpl`
- `AccountBalanceRepositoryImpl`

이 구조를 보면:

- 도메인에서는 `AccountRepository` 같은 인터페이스만 본다.
- 실제 구현은 인프라 계층에서 Spring Data JPA를 사용한다.

즉, 도메인과 기술 구현을 분리한 것이다.

## 4. 왜 `findByAccountIdForUpdate` 가 중요한가

출금과 이체는 동시에 여러 요청이 들어오면 잔액 충돌이 날 수 있다.
그래서 잔액을 읽을 때 단순 조회가 아니라 잠금 조회가 필요하다.

이번에는 `SpringDataAccountBalanceJpaRepository` 에서 아래처럼 처리했다.

- `@Lock(LockModeType.PESSIMISTIC_WRITE)`

이 의미는, 해당 잔액 행을 수정하려는 다른 트랜잭션이 동시에 들어오면 충돌을 피하기 위해 대기하도록 하겠다는 뜻이다.

즉, 동시성 제어의 첫 번째 장치를 실제 구현한 것이다.

## 5. 거래계 JPA 구현체 구조

거래계에서는 아래 구성을 만들었다.

- `SpringDataBankTransactionJpaRepository`
- `SpringDataTransactionEntryJpaRepository`
- `SpringDataAccountLedgerJpaRepository`

그리고 이를 감싸는 어댑터 클래스도 만들었다.

- `BankTransactionRepositoryImpl`
- `TransactionEntryRepositoryImpl`
- `AccountLedgerRepositoryImpl`

이 저장소들은 각각 아래 역할을 맡는다.

- 거래 헤더 저장
- 거래 엔트리 여러 건 저장
- 계좌 원장 여러 건 저장

## 6. 왜 `TransactionPostingProcessorImpl` 이 필요한가

이전 단계에서 `TransferApplicationService` 는 전체 흐름만 가지고 있었고, 실제 포스팅 세부 로직은 비어 있었다.
이번에 `TransactionPostingProcessorImpl` 을 만들어 아래 작업을 실제로 수행하도록 했다.

### 6.1 입금

- 계좌 잔액 증가
- 거래 엔트리 생성
- 계좌 원장 생성
- 잔액 저장

### 6.2 출금

- 계좌 잔액 감소
- 거래 엔트리 생성
- 계좌 원장 생성
- 잔액 저장

### 6.3 이체

- 출금 계좌 잔액 감소
- 입금 계좌 잔액 증가
- 출금 엔트리 생성
- 입금 엔트리 생성
- 출금 원장 생성
- 입금 원장 생성
- 양쪽 잔액 저장

즉, 이제는 단순 스켈레톤이 아니라 실제 거래 반영의 첫 번째 구현이 들어간 상태다.

## 7. 왜 `AccountBalance` 에 메서드를 추가했는가

이번에는 `AccountBalance` 에 아래 메서드를 넣었다.

- `deposit`
- `withdraw`

이렇게 한 이유는 잔액 변경을 외부에서 아무렇게나 필드 수정하지 않게 하기 위해서다.

즉:

- 서비스는 "입금하라", "출금하라"를 요청한다.
- 잔액 엔티티는 내부 규칙에 따라 상태를 바꾼다.

이 방식이 객체지향적으로 더 자연스럽고, 나중에 검증 규칙을 추가하기도 쉽다.

## 8. 서비스 흐름이 어떻게 바뀌었는가

이전에는 서비스가 거래를 만든 뒤 포스팅 처리기에 넘기고 마지막에 저장하는 구조였다.
이번에는 흐름을 아래처럼 조금 더 현실적으로 바꿨다.

1. 거래 헤더를 먼저 `REQUESTED` 상태로 저장
2. 포스팅 처리기가 엔트리, 원장, 잔액을 반영
3. 거래 상태를 `POSTED` 로 변경
4. 거래 헤더를 다시 저장

이렇게 하면 포스팅 시점에 거래 식별자가 이미 존재하므로:

- `last_transaction_id`
- 엔트리의 거래 참조
- 원장의 거래 참조

를 더 자연스럽게 연결할 수 있다.

## 9. 이번 단계에서 아직 남아 있는 것

이번에도 아직 남아 있는 작업이 있다.

- 실제 예외 클래스를 분리하지 않음
- 중복 요청 처리와 멱등 재사용 로직 없음
- 컨트롤러 없음
- DTO 없음
- 테스트 코드 없음

즉, 지금은 "구조와 1차 반영 흐름"까지 왔고, 다음에는 안정성과 외부 API 쪽으로 확장하게 된다.

## 10. 학습 포인트

- 도메인 인터페이스와 JPA 구현체를 분리하면 구조가 더 유연해진다.
- Spring Data JPA는 저장소 구현을 빠르게 만드는 도구다.
- `@Lock` 은 동시성 제어에서 중요한 역할을 한다.
- 응용 서비스와 반영 처리기를 분리하면 서비스가 너무 비대해지는 것을 막을 수 있다.
- 코어 뱅킹의 실제 핵심은 "잔액 숫자 하나"가 아니라 "거래 헤더, 엔트리, 원장, 잔액 갱신의 일관된 흐름"이다.

## 11. 다음 작업

이제 다음 단계로 가장 자연스러운 작업은 아래다.

1. 예외 체계 정리
2. 결과 객체와 응답 모델 정리
3. 채널 API에서 입금, 출금, 이체 컨트롤러 만들기
4. 멱등 처리 로직 추가

