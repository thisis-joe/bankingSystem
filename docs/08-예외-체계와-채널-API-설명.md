# 예외 체계와 채널 API 설명

## 1. 이번 작업의 목적

이번 작업의 목적은 내부 도메인과 서비스 구조를 외부 HTTP 요청과 연결하는 것이다.
지금까지는 계좌, 거래, 저장소, 반영 처리기까지 내부 구조를 만들었다.
이제는 외부에서 요청이 들어왔을 때:

- 어떤 입력 형식을 받을지
- 어떤 서비스로 연결할지
- 성공하면 어떤 응답을 줄지
- 실패하면 어떤 에러 형식으로 내려줄지

를 정리해야 한다.

그래서 이번에는 `예외 체계`, `결과 객체`, `채널 API 컨트롤러`, `전역 예외 처리기`를 함께 만들었다.

## 2. 왜 예외 체계가 먼저 필요한가

컨트롤러를 먼저 만들고 예외를 나중에 정리하면, 오류 응답이 여기저기 흩어지기 쉽다.

예를 들면 다음 문제가 생긴다.

- 어떤 곳은 400
- 어떤 곳은 500
- 어떤 곳은 그냥 문자열 반환
- 어떤 곳은 예외 메시지 그대로 노출

이렇게 되면 API를 사용하는 쪽도 불편하고, 내부 구조도 일관성을 잃는다.

그래서 이번에는 먼저 공통 예외 구조를 만들었다.

## 3. 이번에 만든 예외 구조

### 3.1 `ErrorCode`

오류 코드를 한 곳에서 관리하는 enum 이다.

이번에는 아래 항목을 넣었다.

- 계좌 없음
- 계좌 잔액 없음
- 잘못된 금액
- 통화 불일치
- 잔액 부족
- 동일 계좌 이체
- 검증 오류
- 서버 내부 오류

각 오류 코드에는 아래 정보가 들어간다.

- 코드 문자열
- 기본 메시지
- HTTP 상태 코드

즉, 어떤 예외가 발생했을 때 API 응답을 어떻게 만들지 기준을 한 곳에 모아둔 것이다.

### 3.2 `BankingException`

모든 업무 예외의 공통 부모 클래스다.

이 클래스는 아래를 가진다.

- `ErrorCode`
- 예외 메시지

즉, "무슨 오류인지" 와 "어떤 코드로 내려야 하는지" 를 함께 묶는다.

### 3.3 구체 예외 클래스

이번에는 아래 구체 예외를 추가했다.

- `AccountNotFoundException`
- `AccountBalanceNotFoundException`
- `InvalidAmountException`
- `CurrencyMismatchException`
- `InsufficientBalanceException`
- `SameAccountTransferException`

이렇게 나눈 이유는 코드 가독성을 높이기 위해서다.

예를 들어:

- `throw new InvalidAmountException();`
- `throw new InsufficientBalanceException();`

처럼 읽히면, 왜 실패했는지가 즉시 보인다.

## 4. 서비스가 어떻게 바뀌었는가

이전에는 서비스에서 `IllegalArgumentException`, `IllegalStateException` 을 직접 던졌다.
이번에는 그것을 공통 예외 체계로 바꿨다.

즉:

- 계좌 없음 -> `AccountNotFoundException`
- 잔액 없음 -> `AccountBalanceNotFoundException`
- 금액 오류 -> `InvalidAmountException`
- 통화 불일치 -> `CurrencyMismatchException`
- 잔액 부족 -> `InsufficientBalanceException`
- 같은 계좌 이체 -> `SameAccountTransferException`

이렇게 바꾸면 컨트롤러 쪽에서 예외를 훨씬 일관되게 처리할 수 있다.

## 5. 왜 결과 객체가 필요한가

서비스가 엔티티를 그대로 반환하면 몇 가지 문제가 생긴다.

- API 응답에 불필요한 내부 정보가 노출될 수 있다.
- 지연 로딩 관계 때문에 응답 변환 중 문제가 생길 수 있다.
- 엔티티 구조가 바뀌면 API 응답도 함께 흔들린다.

그래서 이번에는 `TransferResult` 를 만들었다.

이 객체는 서비스가 외부로 넘길 핵심 정보만 담는다.

- 거래 식별자
- 거래 유형
- 거래 상태
- 요청 식별자
- 멱등 키
- 업무 일자
- 발생 시각
- 반영 시각
- 설명

즉, 엔티티를 직접 노출하지 않고, 서비스 결과를 표현하는 별도 객체를 둔 것이다.

## 6. 왜 DTO를 따로 만들었는가

HTTP 요청과 응답은 도메인 엔티티와 같은 것이 아니다.

요청 DTO를 따로 두는 이유:

- 입력 검증을 붙이기 쉽다.
- API 형식과 내부 모델을 분리할 수 있다.
- 엔티티를 직접 바인딩하지 않아 안전하다.

이번에 만든 요청 DTO는 아래다.

- `DepositRequest`
- `WithdrawalRequest`
- `TransferRequest`

응답 DTO는 아래다.

- `TransferResponse`
- `ApiErrorResponse`

## 7. 채널 API 컨트롤러는 무슨 역할을 하는가

이번에 `TransactionController` 를 추가했다.

이 컨트롤러는 아래 역할을 한다.

1. HTTP 요청 받기
2. 헤더와 바디 읽기
3. 요청 DTO 검증
4. Command 객체 만들기
5. 응용 서비스 호출
6. 결과 객체를 응답 DTO로 바꾸기

즉, 컨트롤러는 입력과 출력의 경계 역할을 한다.
비즈니스 규칙 자체는 서비스에 두고, 컨트롤러는 연결만 담당한다.

## 8. API 경로를 어떻게 잡았는가

이번에는 아래 경로를 만들었다.

- `POST /api/v1/transactions/accounts/{accountNo}/deposit`
- `POST /api/v1/transactions/accounts/{accountNo}/withdraw`
- `POST /api/v1/transactions/transfer`

헤더는 아래를 받는다.

- `X-Request-Id`
- `Idempotency-Key` 선택값

여기서 `X-Request-Id` 는 요청 추적용이다.
`Idempotency-Key` 는 아직 완전한 재사용 로직까지 구현하지는 않았지만, 다음 단계 멱등 처리 확장을 위해 미리 받아 두었다.

## 9. 왜 채널 타입을 Command 에 추가했는가

이전에는 서비스가 거래를 만들 때 채널 타입을 고정값으로 넣고 있었다.
하지만 채널계로 올라오면 이 정보는 요청이 들어온 위치에 따라 달라질 수 있다.

예를 들면:

- 오픈 API
- 모바일 뱅킹
- 인터넷 뱅킹
- 배치

그래서 이번에는 `DepositCommand`, `WithdrawalCommand`, `TransferCommand` 에 `channelType` 을 추가했다.
현재 컨트롤러에서는 `OPEN_API` 를 넣고 있다.

즉, 서비스가 채널 정보를 하드코딩하지 않게 바꾼 것이다.

## 10. 전역 예외 처리기는 왜 필요한가

이번에는 `GlobalExceptionHandler` 를 만들었다.

이 클래스는 예외가 발생했을 때 응답을 한곳에서 통일해서 만든다.

처리하는 경우는 아래와 같다.

### 10.1 `BankingException`

업무 예외다.
여기서는 `ErrorCode` 에 들어 있는 HTTP 상태 코드와 메시지를 사용한다.

### 10.2 `MethodArgumentNotValidException`

요청 DTO 검증 실패다.
예를 들어 금액이 비어 있거나 음수일 때 여기에 걸린다.

### 10.3 그 외 예외

예상하지 못한 예외는 내부 오류로 처리한다.

이렇게 하면 컨트롤러마다 try-catch 를 반복하지 않아도 된다.

## 11. 지금 흐름을 한 줄로 정리하면

현재 입금, 출금, 이체 요청 흐름은 아래처럼 된다.

1. 컨트롤러가 요청을 받는다.
2. DTO 검증을 한다.
3. Command 객체를 만든다.
4. 응용 서비스가 검증과 거래 흐름을 조합한다.
5. 포스팅 처리기가 엔트리, 원장, 잔액을 반영한다.
6. 결과 객체를 응답 DTO로 변환한다.
7. 예외가 나면 전역 예외 처리기가 공통 형식으로 응답한다.

## 12. 이번 단계에서 아직 남아 있는 것

이번에도 아직 구현하지 않은 항목이 있다.

- 멱등 키 재사용 로직
- 중복 요청 조회 후 기존 결과 반환
- 인증과 인가
- API 사용 로그 저장
- 테스트 코드

즉, 지금은 "기본적인 채널 API 입구" 까지 만든 상태다.

## 13. 학습 포인트

- 예외 체계를 먼저 잡으면 API 일관성이 좋아진다.
- 엔티티를 직접 응답으로 내보내기보다 결과 객체와 DTO를 두는 편이 안전하다.
- 컨트롤러는 비즈니스 로직을 구현하는 곳이 아니라 입력과 출력의 경계다.
- 전역 예외 처리기는 컨트롤러 중복 코드를 줄여 준다.
- 채널계는 계정계 서비스를 호출하는 진입 계층이라는 점을 코드로 체감할 수 있다.

## 14. 다음 작업

이제 다음 단계로 가장 자연스러운 작업은 아래다.

1. 멱등 처리 로직 추가
2. API 사용 이력 저장
3. 채널 인증과 인가 구조 추가
4. 테스트 코드 작성

