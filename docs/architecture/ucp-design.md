# UCP 기반 쇼핑몰 설계 문서 (초안)

## 1) UCP란?
UCP(Universal Commerce Protocol)는 전자상거래에서 플랫폼(에이전트/검색/마켓플레이스)과
비즈니스(쇼핑몰)가 주고받는 계약과 흐름을 표준화한 프로토콜이다.

핵심은 "구글 구현을 가져다 쓰는 것"이 아니라,
우리 쇼핑몰이 UCP 스펙에 맞춘 API/데이터/흐름을 제공하도록 설계하는 것이다.

참고: UCP는 초기 핵심 Capability로 Checkout, Identity Linking, Order를 정의한다.
이 문서는 UCP 프로토콜 버전 2026-01-11을 기준으로 정리한다.

## 2) 적용 원칙 (우리 프로젝트 기준)
1. 내부 구현은 자유, 외부 계약은 UCP 형태에 맞춘다.
2. 과한 스펙은 제외하고, 나중에 확장 가능한 구조로 만든다.
3. 최소 흐름을 UCP 기준으로 잡고, 기능은 단계적으로 붙인다.

## 3) 큰 흐름 (UCP 기준)
Catalog -> Cart -> Checkout -> Order -> Fulfillment/Adjustment

- UCP의 초기 핵심 Capability는 Checkout/Identity/Order다.
- Catalog/Cart는 내부 도메인으로 설계하되, Checkout line_items에 맞춘다.
- Checkout/Order는 UCP 표준 Capability에 맞춰 구현한다.

## 4) Capability별 최소 설계

### 4.1 Checkout (필수)
목표: 장바구니와 결제 직전까지의 세션을 표준 구조로 관리

최소 동작
- Create Checkout
- Get Checkout
- Update Checkout
- Complete Checkout
- Cancel Checkout

최소 상태 모델
- incomplete -> ready_for_complete -> complete_in_progress -> cooizmpleted
- 필요 시: requires_escalation (사용자 UI 전환)
- 취소 흐름: canceled

핵심 필드(최소)
- id
- line_items (상품/수량/가격)
- currency
- totals
- status
- messages (옵션)
- continue_url (requires_escalation 시 필수)
- links (약관/개인정보/환불 정책 링크)
- expires_at (옵션, RFC 3339)
- payment (응답에 포함, 결제 핸들러 규칙에 맞춤)

비즈니스 필수 요건(요약)
- Checkout 완료 시 확인 이메일 발송
- requires_escalation 상태에서는 반드시 continue_url 제공
- requires_escalation 시 messages 포함 (에러 핸들링 규칙 준수)
- Checkout 생성 시 결제 구성(payment.handlers)을 명시
- Checkout 완료는 신뢰 가능한 UI를 통해 진행 (AP2 예외)

### 4.2 Order (필수)
목표: 주문 확정 이후의 상태와 이벤트 기록 표준화

최소 구조
- id, checkout_id, permalink_url
- line_items (불변)
- fulfillment (expectations + events)
- adjustments (환불/취소 등 이벤트 로그)
- totals

주문 이벤트 전송
- 플랫폼이 제공한 webhook URL로 주문 이벤트를 전송
- 이벤트에는 전체 Order 엔티티를 포함
- 웹훅 요청은 `Request-Signature` 헤더로 서명 (detached JWT)
- "Order created" 이벤트는 반드시 발행
- 실패한 웹훅은 재시도

### 4.3 Identity Linking (보류, 설계만)
목표: 플랫폼 계정과 쇼핑몰 계정을 OAuth 2.0으로 연결

현재는 "확장 포인트"만 설계
- 계정 식별자 분리 (user_id vs platform_subject)
- 토큰 기반 접근을 위한 auth 경계 분리

## 5) 내부 도메인 모델링 가이드

### Product/Catalog
UCP Line Item에 매핑 가능한 형태로 설계
- product_id, title, image_url, price, currency
- inventory, attributes (옵션)

### Cart
Checkout line_items에 그대로 매핑 가능하도록
- cart_id, items[], totals

### Order
Checkout -> Order 변환 로직은 단방향
- 주문 생성 이후 line_items는 불변
- 상태 변경은 fulfillment/adjustment 이벤트로 축적

## 6) Discovery/계약 표준 (요약)
- Business Profile을 `/.well-known/ucp`에 게시
- Profile에 UCP 버전, 서비스, Capability 목록, REST 엔드포인트/스키마 URL 포함
- 플랫폼/비즈니스 모두 `signing_keys`를 공개 (웹훅 서명 키 회전 포함)
- 결제 핸들러는 `payment.handlers`로 선언
- REST/MCP/A2A/Embedded 바인딩 중 필요한 것만 노출
- 플랫폼은 매 요청마다 profile URI를 광고 (UCP-Agent 헤더 또는 MCP 메타)
- 모든 UCP 응답은 `ucp` 객체로 버전과 지원 Capability를 반환
- Business는 요청의 Capability 목록과 교집합으로 응답

## 7) 프로토콜/보안 기준 (요약)
- 모든 통신은 HTTPS
- 날짜는 RFC 3339, 금액은 최소 단위(센트 등)
- Capability 버전은 날짜 기반(YYYY-MM-DD)
- 플랫폼과의 Capability 협상은 프로파일 기반으로 진행
- Business -> Platform Webhook 호출은 서명 검증을 전제로 설계

## 8) 단계적 구현 로드맵 (초안)

Phase 1: 최소 Checkout + Order
- 상품 조회, 장바구니, Checkout 생성/갱신/완료
- 주문 생성 및 조회

Phase 2: Fulfillment/Adjustment 이벤트
- 배송 이벤트, 취소/환불 이벤트 추가

Phase 3: Identity Linking
- OAuth 기반 계정 연결

## 9) 문서 목적
이 문서는 UCP 스펙을 "기준선"으로 삼아
우리 쇼핑몰의 도메인 모델과 흐름을 정렬하는 것을 목표로 한다.
초기 구현은 최소 스펙으로 시작하고, 필요 시 UCP 기준으로 확장한다.
