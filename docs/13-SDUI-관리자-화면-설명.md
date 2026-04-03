# SDUI 관리자 화면 설명

## 1. 이번 작업의 목적

이번 작업의 목적은 `코어 뱅킹 운영 현황을 한 페이지에서 보는 관리자 화면` 을 만들되, 그 화면을 일반적인 프론트엔드 고정 화면이 아니라 `Server-Driven UI(SDUI)` 방식으로 구성하는 것이다.

여기서 중요한 것은 단순히 화면 하나를 만드는 것이 아니다.
이번 작업의 진짜 목표는 아래 두 가지다.

1. SDUI가 무엇인지 코드로 이해할 수 있게 만들기
2. 왜 관리자 화면이 SDUI와 잘 맞는지 직접 체감할 수 있게 만들기

사용자가 이번 단계에서 특히 `개념 이해` 가 중요하다고 했기 때문에, 이번 구현은 기능보다 구조가 더 잘 보이도록 구성했다.

## 2. SDUI란 무엇인가

SDUI는 쉽게 말하면 `서버가 화면을 설명하고, 클라이언트는 그 설명을 그리는 방식` 이다.

보통 일반적인 프론트엔드는 아래처럼 동작한다.

- 프론트엔드 코드 안에 화면 구조가 이미 정해져 있음
- 서버는 그 안에 들어갈 데이터만 내려줌

예를 들어:

- 프론트엔드가 "상단 카드 4개, 아래 표 2개, 오른쪽 타임라인" 구조를 미리 알고 있음
- 서버는 숫자와 목록만 내려줌

반면 SDUI는 아래처럼 생각한다.

- 서버가 "이 화면은 카드 블록, 표 블록, 타임라인 블록, 액션 블록으로 구성된다" 는 `화면 정의` 를 JSON으로 내려줌
- 클라이언트는 JSON을 해석해서 실제 HTML을 그림

즉:

- 일반 UI: 서버는 데이터 제공자
- SDUI: 서버는 데이터 + 화면 구조 제공자

이 차이가 핵심이다.

## 3. 왜 관리자 화면이 SDUI와 잘 맞는가

사용자가 참고로 준 글도 같은 방향을 이야기한다.
관리자 화면은 서비스 메인 화면보다 `정책 변화`, `권한 차이`, `운영 상황`, `배치 상태`, `기능 토글` 의 영향을 많이 받기 때문에 SDUI와 잘 맞는다.

참고:

- [Server-Driven UI for Admin](https://blog.selectfromuser.com/server-driven-ui-for-admin/)

이유를 코어 뱅킹 운영 화면 관점으로 바꾸면 아래와 같다.

### 3.1 관리자 화면은 자주 바뀐다

운영 화면은 아래 이유로 구조가 자주 바뀐다.

- 오늘은 거래 정합성 경고를 맨 위에 올려야 할 수 있음
- 내일은 API 오류율 블록이 더 중요할 수 있음
- 특정 배치 기간에는 자동이체 상태 블록을 강조해야 할 수 있음
- 특정 운영자 그룹은 어떤 블록을 보고, 다른 그룹은 다른 블록을 봐야 할 수 있음

이런 화면을 프론트엔드 코드에만 고정해 두면 변경 비용이 계속 커진다.

### 3.2 관리자 화면은 "정보 구조" 가 중요하다

관리자 화면은 화려한 사용자 경험보다 아래가 중요하다.

- 무엇이 먼저 보여야 하는가
- 어떤 경고가 어느 표로 가야 하는가
- 어떤 블록이 같은 행에 배치되어야 하는가
- 어떤 액션이 지금 시점에 노출되어야 하는가

즉, 관리자 UI는 디자인보다도 `운영 정보 설계` 의 비중이 크다.
이런 성격은 서버에서 제어하기 좋다.

### 3.3 백엔드가 업무 맥락을 가장 잘 안다

코어 뱅킹에서는 화면에 무엇이 보여야 하는지 판단하려면 아래 맥락이 필요하다.

- 원장 반영 상태
- 잔액 정합성 경고
- 멱등 충돌 추적
- 채널 요청 이력
- 자동이체 지연 여부

이런 정보는 원래 백엔드가 가장 잘 알고 있다.
그래서 운영용 화면은 서버가 "무엇을 보여줄지" 결정하는 편이 자연스럽다.

## 4. 이번 예제에서 SDUI를 어떻게 구현했는가

이번 예제는 SDUI를 학습하기 쉬운 형태로 단순화했다.

전체 구조는 아래와 같다.

1. `information-read` 가 읽기 모델 데이터를 만든다.
2. `channel-api` 가 그 데이터를 SDUI JSON 스펙으로 변환한다.
3. `bootstrap/static/sdui-admin` 이 그 JSON을 받아 렌더링한다.

즉, 세 층이 있다.

- 데이터 층: 지금 화면에 보여 줄 운영 정보
- 화면 정의 층: 그 정보를 어떤 블록으로 배치할지
- 렌더링 층: JSON을 HTML/CSS로 그림

이 분리가 SDUI를 이해하는 데 가장 중요하다.

## 5. 이번 구조를 파일 기준으로 보면 어떻게 되는가

### 5.1 읽기 모델 데이터

`information-read` 모듈에서 대시보드용 읽기 모델을 만들었다.

주요 파일:

- [CoreBankingOverviewReadService.java](C:\SSAFY\joseph\workspaces\BankingSystem\information-read\src\main\java\com\bankingsystem\information\read\application\service\CoreBankingOverviewReadService.java)
- [CoreBankingOverviewSnapshot.java](C:\SSAFY\joseph\workspaces\BankingSystem\information-read\src\main\java\com\bankingsystem\information\read\application\model\CoreBankingOverviewSnapshot.java)
- [DashboardMetric.java](C:\SSAFY\joseph\workspaces\BankingSystem\information-read\src\main\java\com\bankingsystem\information\read\application\model\DashboardMetric.java)
- [AccountIntegrityAlert.java](C:\SSAFY\joseph\workspaces\BankingSystem\information-read\src\main\java\com\bankingsystem\information\read\application\model\AccountIntegrityAlert.java)

이 층의 책임은 `무슨 데이터가 필요한가` 이다.

예를 들어:

- 활성 계좌 수
- 당일 원장 반영 수
- 정합성 경고 목록
- 최근 거래 흐름
- API 요청 흐름

아직은 학습용 샘플 데이터를 코드에서 반환하지만, 구조상으로는 나중에 DB 조회나 읽기 모델 테이블로 바꾸기 쉽게 되어 있다.

### 5.2 화면 정의 JSON 생성

`channel-api` 모듈에서 읽기 모델을 SDUI 화면 정의로 바꾼다.

주요 파일:

- [CoreBankingAdminScreenService.java](C:\SSAFY\joseph\workspaces\BankingSystem\channel-api\src\main\java\com\bankingsystem\channel\api\application\service\CoreBankingAdminScreenService.java)
- [AdminScreenController.java](C:\SSAFY\joseph\workspaces\BankingSystem\channel-api\src\main\java\com\bankingsystem\channel\api\presentation\controller\AdminScreenController.java)

여기서 서버는 아래 같은 JSON을 만든다.

- 화면 ID
- 제목
- 부제목
- 레이아웃 이름
- 블록 목록
- 각 블록의 컴포넌트 타입
- 각 블록이 렌더링할 데이터

즉, 서버는 더 이상 "숫자 목록" 만 주는 것이 아니라 "이 숫자를 metric-grid 로, 이 목록을 table 로, 이 내역을 timeline 으로 보여라" 라는 수준까지 말해 준다.

### 5.3 브라우저 렌더러

브라우저 쪽은 아주 얇게 만들었다.

주요 파일:

- [index.html](C:\SSAFY\joseph\workspaces\BankingSystem\bootstrap\src\main\resources\static\sdui-admin\index.html)
- [styles.css](C:\SSAFY\joseph\workspaces\BankingSystem\bootstrap\src\main\resources\static\sdui-admin\styles.css)
- [app.js](C:\SSAFY\joseph\workspaces\BankingSystem\bootstrap\src\main\resources\static\sdui-admin\app.js)

이 렌더러의 책임은 아래뿐이다.

- JSON 가져오기
- `component` 값 보기
- 그 값에 맞는 렌더 함수 호출
- HTML 만들기

즉, 브라우저는 "이 블록이 왜 필요한가" 를 몰라도 된다.
서버가 내려준 설명을 그리기만 하면 된다.

## 6. 이번 SDUI 화면은 어떤 JSON 구조인가

이번 예제의 화면 정의는 아래 구조를 가진다.

```json
{
  "screenId": "core-banking-overview",
  "title": "코어 뱅킹 운영 현황",
  "blocks": [
    {
      "component": "metric-grid",
      "title": "핵심 운영 지표",
      "items": []
    },
    {
      "component": "table",
      "title": "정합성 경고",
      "columns": [],
      "rows": []
    }
  ]
}
```

핵심은 `component` 이다.
브라우저는 이 값을 보고 어떤 렌더 함수를 써야 하는지 결정한다.

이번에 지원하는 컴포넌트는 아래 다섯 가지다.

- `metric-grid`
- `table`
- `timeline`
- `fact-list`
- `action-list`

즉, SDUI에서는 `JSON 스키마` 와 `렌더러가 아는 컴포넌트 집합` 이 계약이 된다.

## 7. 이번 한 페이지 디자인은 왜 이렇게 구성했는가

사용자가 원한 방향은 아래였다.

- 매우 시스템적인 느낌
- 한 페이지 안에서 모든 것이 확인 가능

그래서 화면을 `운영 관제판` 처럼 설계했다.

상단:

- 가장 중요한 운영 지표를 카드형으로 즉시 노출

중단:

- 정합성 경고 표
- 최근 거래 타임라인
- 최근 채널 API 흐름 표

하단:

- SDUI 구조 설명 블록
- 즉시 실행 가능한 액션 블록

이 구조를 택한 이유는 운영자가 아래 질문에 빠르게 답하도록 하기 위해서다.

- 지금 시스템은 안정적인가
- 어디에 경고가 있는가
- 최근 거래는 어떤 흐름인가
- 채널 요청은 정상인가
- 지금 바로 할 수 있는 액션은 무엇인가

즉, 단순 예쁜 대시보드가 아니라 `판단용 화면` 으로 구성했다.

## 8. 왜 프론트엔드를 최대한 얇게 두었는가

이번 작업은 프론트엔드 자체를 깊게 만드는 것이 목적이 아니다.
오히려 SDUI의 핵심을 보이게 하려면 프론트는 얇아야 한다.

프론트가 두꺼워지면 아래 문제가 생긴다.

- 서버가 어떤 구조를 줬는지보다 프론트 코드 자체가 더 중요해짐
- SDUI보다 SPA 프레임워크 학습으로 초점이 옮겨감
- "서버가 주도한다" 는 핵심이 흐려짐

그래서 이번에는 프론트엔드를 아래 수준으로 제한했다.

- 순수 HTML
- 순수 CSS
- 순수 JavaScript
- JSON 해석기 역할의 렌더 함수

이렇게 해야 SDUI의 본질이 선명하게 보인다.

## 9. 이번 단계에서 SDUI와 일반 API의 차이를 어떻게 체감할 수 있는가

이번 프로젝트에는 이미 거래 API 같은 일반 API가 있다.

예를 들어 거래 API는 이런 식이다.

- 입금 요청을 받음
- 결과 데이터를 반환함

이건 일반적인 데이터 API다.

반면 이번 SDUI API는 이런 식이다.

- 화면 전체 구성을 설명함
- 어떤 블록을 그릴지 알려 줌
- 각 블록에 어떤 데이터를 붙일지 같이 내려줌

즉, 같은 JSON이라도 목적이 다르다.

- 일반 API JSON: 업무 데이터 전달
- SDUI JSON: 화면 구조 전달

## 10. 이번 구현에서 중요한 원리

### 10.1 서버가 "화면의 의도" 를 가진다

이번 서버 JSON은 단순 데이터 묶음이 아니라 아래 의도를 담고 있다.

- 이 화면은 운영 현황 화면이다
- 상단은 핵심 지표다
- 이 표는 정합성 경고다
- 이 목록은 운영 액션이다

즉, 서버가 단순 저장소가 아니라 `화면 편집자` 의 역할도 일부 맡는다.

### 10.2 클라이언트는 "범용 렌더러" 가 된다

브라우저는 화면별로 새로운 HTML을 직접 만들지 않는다.
대신 "metric-grid 는 이렇게, table 은 이렇게" 같은 범용 규칙만 가진다.

이 구조가 중요한 이유는 화면이 늘어날수록 재사용성이 커지기 때문이다.

### 10.3 읽기 모델이 중요해진다

SDUI에서는 서버가 화면까지 책임지므로, 보여 줄 데이터를 잘 모은 읽기 모델이 중요해진다.

이번에 `information-read` 를 사용한 이유도 여기에 있다.
화면이 복잡할수록 쓰기 모델보다 읽기 모델이 더 중요해질 수 있다.

## 11. 이번 단계에서 보안 설정을 같이 손본 이유

실제로 SDUI 화면을 띄워 확인하려면 애플리케이션이 실행돼야 한다.
이번 프로젝트에는 `channel-security` 모듈이 이미 있었고 Spring Security 기본 설정 때문에 처음에는 401 이 발생했다.

그래서 이번에는 학습용으로 아래를 같이 정리했다.

- 실행 모듈에서 JPA 엔티티/리포지토리 스캔 범위 명시
- H2 런타임 데이터베이스 설정 추가
- 보안 설정에서 현재는 모든 요청 허용

여기서 중요한 것은 이 보안 설정이 `최종형` 이 아니라 `학습용 통과 설정` 이라는 점이다.
다음 단계에서 실제 인증을 붙이면 다시 엄격하게 바뀔 수 있다.

## 12. 실제로 무엇이 검증되었는가

이번에는 단순 컴파일만 본 것이 아니라 실행도 확인했다.

검증한 것:

- `.\gradlew.bat test` 성공
- Spring Boot 실행 성공
- `/api/v1/admin/screens/core-banking-overview` 가 200 응답
- `/sdui-admin/index.html` 가 200 응답

즉, 이번 SDUI 예제는 코드만 있는 상태가 아니라 실제로 열어 볼 수 있는 상태다.

## 13. 어디를 먼저 보면 좋은가

SDUI를 처음 이해하려면 아래 순서가 좋다.

1. [CoreBankingOverviewReadService.java](C:\SSAFY\joseph\workspaces\BankingSystem\information-read\src\main\java\com\bankingsystem\information\read\application\service\CoreBankingOverviewReadService.java)
2. [CoreBankingAdminScreenService.java](C:\SSAFY\joseph\workspaces\BankingSystem\channel-api\src\main\java\com\bankingsystem\channel\api\application\service\CoreBankingAdminScreenService.java)
3. [AdminScreenController.java](C:\SSAFY\joseph\workspaces\BankingSystem\channel-api\src\main\java\com\bankingsystem\channel\api\presentation\controller\AdminScreenController.java)
4. [app.js](C:\SSAFY\joseph\workspaces\BankingSystem\bootstrap\src\main\resources\static\sdui-admin\app.js)
5. [styles.css](C:\SSAFY\joseph\workspaces\BankingSystem\bootstrap\src\main\resources\static\sdui-admin\styles.css)

이 순서대로 보면 아래 흐름이 보인다.

- 데이터 생성
- 화면 정의 생성
- API 노출
- 브라우저 렌더링
- 화면 스타일링

## 14. 학습 포인트

- SDUI는 서버가 화면 구조를 JSON으로 설명하는 방식이다.
- 관리자 화면은 정책과 레이아웃 변화가 잦아 SDUI와 잘 맞는다.
- SDUI에서는 `데이터`, `화면 정의`, `렌더러` 를 분리해 생각하는 것이 중요하다.
- 클라이언트는 범용 렌더러가 되고, 서버는 화면 구성 책임을 더 많이 가진다.
- SDUI를 이해하려면 일반 데이터 API와 `화면 정의 API` 의 차이를 구분해야 한다.

## 15. 다음 작업 후보

이제 다음으로 자연스러운 작업은 아래 중 하나다.

1. SDUI JSON 스키마를 더 엄격하게 타입화하기
2. 권한별로 다른 블록을 내려주는 SDUI 정책 추가
3. 실제 DB 읽기 모델과 연결해 운영 현황을 실데이터로 바꾸기
4. 버튼 액션도 서버 정의 기반으로 더 확장하기

현재 가장 학습 효과가 큰 다음 단계는 `권한별 블록 제어` 다.
이유는 SDUI의 장점이 가장 잘 드러나는 부분이 "같은 앱이라도 사용자나 역할에 따라 서버가 다른 화면 구성을 내려주는 것" 이기 때문이다.
