# NextMall 프로젝트 리뷰 (HEAD 기준) - 2026-01-23

범위
- git HEAD 커밋 기준만 리뷰 (작업 중 변경 사항 제외).
- 보안, 일관성, 품질 리스크 중심으로 확인.

요약
- High: 3
- Medium: 15
- Low: 12

High
1) checkout-service에 Passport 토큰 보안 설정이 없음
- 근거: 다른 서비스는 PassportTokenSecurityConfig를 import하지만 checkout-service에는 해당 SecurityConfig가 없음.
- 리스크: Spring Security 기본 설정(기본 로그인)으로 처리되거나 내부 Passport 인증이 일관되게 강제되지 않을 수 있음.
- 참고:
  - services/user-service/src/main/kotlin/com/nextmall/user/config/SecurityConfig.kt:1-11
  - services/order-service/src/main/kotlin/com/nextmall/order/config/SecurityConfig.kt:1-11
  - services/product-service/src/main/kotlin/com/nextmall/product/config/SecurityConfig.kt:1-11

2) 주문 조회/취소에서 소유자 검증 누락
- 근거: getOrder, cancelOrder에 소유자 검증 TODO가 있음.
- 리스크: 인증된 사용자가 다른 사용자의 주문을 조회/취소할 수 있음.
- 참고:
  - services/order-service/src/main/kotlin/com/nextmall/order/presentation/controller/OrderController.kt:25-61

3) 상품 수정/삭제에 권한 컨텍스트 누락
- 근거: update/delete 엔드포인트는 CurrentUser를 받지 않고 권한/소유 검증이 없음. create는 존재.
- 리스크: 인증된 사용자가 다른 사용자의 상품을 수정/삭제 가능.
- 참고:
  - services/product-service/src/main/kotlin/com/nextmall/product/presentation/controller/ProductController.kt:43-83

Medium
4) checkout 요청 검증이 중첩 객체에 전파되지 않음
- 근거: 리스트/중첩 DTO에 @Valid 누락, 수량/금액 제약이 최소 또는 부재.
- 리스크: null, 0/음수, 빈 핸들러 등 잘못된 payload가 유입될 수 있음.
- 참고:
  - services/checkout-service/src/main/kotlin/com/nextmall/checkout/presentation/request/CheckoutRequests.kt:10-31
  - services/checkout-service/src/main/kotlin/com/nextmall/checkout/presentation/request/LineItemRequest.kt:7-16
  - services/checkout-service/src/main/kotlin/com/nextmall/checkout/presentation/request/MoneyRequest.kt:7-11
  - services/checkout-service/src/main/kotlin/com/nextmall/checkout/presentation/request/PaymentRequest.kt:8-17

5) Money 모델이 서비스/모듈별로 다름
- 근거: common Money는 BigDecimal(scale=2), checkout Money는 Long + currency.
- 리스크: 직렬화/비즈니스 규칙이 서비스별로 달라 계약이 흔들릴 수 있음.
- 참고:
  - common/util/src/main/kotlin/com/nextmall/common/util/Money.kt:6-33
  - services/checkout-service/src/main/kotlin/com/nextmall/checkout/domain/model/Money.kt:3-5

6) API JSON 네이밍 전략이 불일치
- 근거: checkout-service는 SNAKE_CASE 강제, 다른 서비스는 기본값 사용.
- 리스크: 클라이언트/BFF에서 서비스별 JSON 규칙을 따로 처리해야 함.
- 참고:
  - services/checkout-service/src/main/resources/application.yml:7-8

7) 응답 ID 타입 불일치
- 근거: user 응답은 String, order/product는 Long.
- 리스크: 프론트/BFF DTO 매핑 비용 증가, ID 형식 혼선.
- 참고:
  - services/user-service/src/main/kotlin/com/nextmall/user/presentation/response/UserViewResponse.kt:5-14
  - services/product-service/src/main/kotlin/com/nextmall/product/presentation/response/ProductViewResponse.kt:6-13
  - services/order-service/src/main/kotlin/com/nextmall/order/presentation/response/OrderViewResponse.kt:7-14

Low
8) Gateway 에러 응답이 공통 에러 계약과 다름
- 근거: Gateway는 BAD_GATEWAY + CommonErrorCode.INTERNAL_ERROR 고정 메시지를 반환.
- 리스크: 클라이언트 에러 처리 불일치, 관측성 저하.
- 참고:
  - services/api-gateway/src/main/kotlin/com/nextmall/apigateway/exception/GatewayGlobalExceptionHandler.kt:15-27
  - services/user-service/src/main/kotlin/com/nextmall/user/exception/UserGlobalExceptionHandler.kt:18-45

9) 문서 상 BFF가 MVC로 표기됨
- 근거: README/CLAUDE에서 MVC로 표기, 실제 build.gradle은 webflux.
- 리스크: 온보딩 혼선, 잘못된 가정으로 작업 가능.
- 참고:
  - README.md:141
  - CLAUDE.md:25
  - services/bff-service/build.gradle.kts:21

Quality / Convention (Additional)
10) Policy enforcement가 authentication.details에서 principal을 읽음
- 근거: PolicyEnforcementAspect가 authentication.details를 AuthenticatedPrincipal로 캐스팅.
- 리스크: 일반적인 패턴(authentication.principal)을 사용할 경우 정책 평가 실패 가능.
- 참고:
  - common/authorization/src/main/kotlin/com/nextmall/common/authorization/aop/PolicyEnforcementAspect.kt:89-102

11) 주문 서비스 DTO 패키지/네이밍 불일치
- 근거: order-service는 presentation/dto + *Snapshot 네이밍, 다른 서비스는 request/response.
- 리스크: API 컨벤션 일관성 저하, DTO 매핑 비용 증가.
- 참고:
  - services/order-service/src/main/kotlin/com/nextmall/order/presentation/dto/CreateOrderSnapshotRequest.kt:1-22
  - services/order-service/src/main/kotlin/com/nextmall/order/presentation/controller/OrderController.kt:3-34
  - services/product-service/src/main/kotlin/com/nextmall/product/presentation/request/CreateProductRequest.kt:1-22

12) 보안 설정 클래스 네이밍 불일치
- 근거: auth-service는 WebSecurityConfig, 다른 서비스는 SecurityConfig.
- 리스크: 탐색성 저하, 컨벤션 불일치.
- 참고:
  - services/auth-service/src/main/kotlin/com/nextmall/auth/config/WebSecurityConfig.kt:11-17
  - services/user-service/src/main/kotlin/com/nextmall/user/config/SecurityConfig.kt:8-11

13) Money DTO 네이밍 불일치
- 근거: order-service는 MoneyAmount, checkout/common은 Money.
- 리스크: 의미 모호, 서비스 간 매핑 혼선.
- 참고:
  - services/order-service/src/main/kotlin/com/nextmall/order/presentation/dto/MoneyAmount.kt:1-5
  - services/checkout-service/src/main/kotlin/com/nextmall/checkout/domain/model/Money.kt:1-5
  - common/util/src/main/kotlin/com/nextmall/common/util/Money.kt:6-33

14) checkout 응답 매핑 로직 중복
- 근거: Checkout.toResponse()와 CheckoutView.toResponse()가 동일한 필드 매핑.
- 리스크: 변경 시 누락/불일치 발생 가능.
- 참고:
  - services/checkout-service/src/main/kotlin/com/nextmall/checkout/presentation/response/CheckoutResponseMapper.kt:15-113

15) Order 금액 요청 DTO에 수치 검증 없음
- 근거: MoneyAmountRequest.amount에 @Min 등 제약 없음.
- 리스크: 음수/0 값이 통과할 수 있음.
- 참고:
  - services/order-service/src/main/kotlin/com/nextmall/order/presentation/dto/MoneyAmountRequest.kt:1-9
  - services/order-service/src/main/kotlin/com/nextmall/order/presentation/dto/OrderTotalsRequest.kt:1-19

16) OrderSnapshot adjustments가 비타입 구조
- 근거: adjustments가 List<Map<String, Any>>로 선언됨.
- 리스크: 약한 타입으로 계약이 불안정해짐.
- 참고:
  - services/order-service/src/main/kotlin/com/nextmall/order/presentation/dto/OrderSnapshot.kt:1-11

17) 소셜 가입 요청 검증 누락
- 근거: SocialSignUpRequest에 검증 어노테이션 없음, 컨트롤러도 @Valid 미사용.
- 리스크: 구현 시 잘못된 요청이 유입될 수 있음.
- 참고:
  - services/bff-service/src/main/kotlin/com/nextmall/bff/presentation/request/signup/SocialSignUpRequest.kt:1-7
  - services/bff-service/src/main/kotlin/com/nextmall/bff/presentation/controller/SignUpController.kt:27-39

Architecture / Commonization (Additional)
18) auth-service와 BFF에 계약 DTO 중복
- 근거: TokenResponse, LoginRequest, RefreshTokenRequest가 양쪽에 중복 정의됨.
- 리스크: 계약 불일치, 변경 시 동시 수정 필요.
- 참고:
  - services/auth-service/src/main/kotlin/com/nextmall/auth/presentation/response/token/TokenResponse.kt:1-6
  - services/bff-service/src/main/kotlin/com/nextmall/bff/presentation/response/login/TokenResponse.kt:1-14
  - services/auth-service/src/main/kotlin/com/nextmall/auth/presentation/request/token/LoginRequest.kt:1-13
  - services/bff-service/src/main/kotlin/com/nextmall/bff/presentation/request/login/LoginRequest.kt:1-21
  - services/auth-service/src/main/kotlin/com/nextmall/auth/presentation/request/token/RefreshTokenRequest.kt:1-8
  - services/bff-service/src/main/kotlin/com/nextmall/bff/presentation/request/token/RefreshTokenRequest.kt:1-8

19) AuthProvider enum 중복
- 근거: auth-service와 BFF가 각자 AuthProvider enum 보유.
- 리스크: Provider 변경 시 동기화 필요.
- 참고:
  - services/auth-service/src/main/kotlin/com/nextmall/auth/domain/account/AuthProvider.kt:1-11
  - services/bff-service/src/main/kotlin/com/nextmall/bff/client/auth/AuthProvider.kt:1-9

20) BFF 내 API 경로 스타일 혼재
- 근거: "/auth/tokens/refresh"와 "/sign-up"가 혼재.
- 리스크: 라우팅 규칙 일관성 저하.
- 참고:
  - services/bff-service/src/main/kotlin/com/nextmall/bff/presentation/controller/AuthController.kt:19-47
  - services/bff-service/src/main/kotlin/com/nextmall/bff/presentation/controller/SignUpController.kt:18-43

21) 에러 코드 → HTTP 상태 매핑이 서비스별로 다름
- 근거: user-service는 AuthorizationErrorCode를 FORBIDDEN으로 매핑, auth-service는 category 기반만 사용.
- 리스크: 유사 에러에 다른 HTTP 상태가 반환될 수 있음.
- 참고:
  - services/user-service/src/main/kotlin/com/nextmall/user/exception/HttpStatusMapper.kt:10-27
  - services/auth-service/src/main/kotlin/com/nextmall/auth/exception/HttpStatusMapper.kt:10-24

Documentation / Information Architecture (Additional)
22) UCP design 문서와 UCP Checkout/Order API 문서 중복
- 근거: ucp-design.md에 checkout/order 규칙이 상세 API 문서와 중복.
- 리스크: 빠른 변경 시 문서 간 계약 불일치 가능.
- 참고:
  - docs/architecture/ucp-design.md:26-79
  - docs/architecture/ucp-checkout-api.md:6-122
  - docs/architecture/ucp-order-api.md:9-76

23) 리뷰 문서가 인덱스 유지에 의존
- 근거: docs/README.md에 리뷰 목록이 있으나, 인덱스 관리가 느슨하면 누락 가능.
- 리스크: 품질 기준 문서가 묻힐 수 있음.
- 참고:
  - docs/README.md:7-62
  - docs/reviews/PROJECT_REVIEW_2026-01-23.md

24) 에러 응답 계약이 운영용으로는 최소
- 근거: ErrorResponse는 code/message만 포함, trace/correlation/path/validation 상세가 없음.
- 리스크: 디버깅과 클라이언트 처리 난이도 증가.
- 참고:
  - common/exception/src/main/kotlin/com/nextmall/common/exception/ErrorResponse.kt:1-6

25) 글로벌 예외 처리 로직이 서비스별로 중복
- 근거: auth/user 서비스가 유사한 GlobalExceptionHandler를 별도 정의.
- 리스크: 에러 처리 정책이 서비스 간에 드리프트할 수 있음.
- 참고:
  - services/auth-service/src/main/kotlin/com/nextmall/auth/exception/AuthGlobalExceptionHandler.kt:1-47
  - services/user-service/src/main/kotlin/com/nextmall/user/exception/UserGlobalExceptionHandler.kt:1-47

Quality / API Consistency (Additional)
26) BFF 주문 API가 userId를 클라이언트 입력으로 받음
- 근거: CreateOrderRequest에 userId가 있고, OrderController가 이를 그대로 사용.
- 리스크: CurrentUser 강제 없이 타 사용자 주문을 요청할 수 있음.
- 참고:
  - services/bff-service/src/main/kotlin/com/nextmall/bff/presentation/request/order/CreateOrderRequest.kt:6-12
  - services/bff-service/src/main/kotlin/com/nextmall/bff/presentation/controller/OrderController.kt:26-55

27) BFF create-order가 201 대신 200 OK 반환
- 근거: createOrder가 ResponseEntity.ok로 반환, order-service는 CREATED 사용.
- 리스크: REST 의미 불일치.
- 참고:
  - services/bff-service/src/main/kotlin/com/nextmall/bff/presentation/controller/OrderController.kt:26-38
  - services/order-service/src/main/kotlin/com/nextmall/order/presentation/controller/OrderController.kt:25-34

28) 사용자 주문 조회 경로가 비표준
- 근거: BFF는 GET /orders/users/{userId} 사용.
- 리스크: 사용자 스코프 리소스 추가 시 경로 일관성 저하.
- 참고:
  - services/bff-service/src/main/kotlin/com/nextmall/bff/presentation/controller/OrderController.kt:49-55

29) Auth 계정 생성 요청에 provider 검증 없음
- 근거: CreateAuthAccountRequest.provider에 @NotNull 없음.
- 리스크: null provider가 애플리케이션 계층까지 들어올 수 있음.
- 참고:
  - services/auth-service/src/main/kotlin/com/nextmall/auth/presentation/request/account/CreateAuthAccountRequest.kt:7-16

Package Structure / Layering (Additional)
30) 서비스 패키지 구조가 모듈별로 불일치
- 근거: order-service는 presentation/dto + *Snapshot, 다른 서비스는 request/response.
- 리스크: 컨벤션 불일치로 온보딩 비용 증가.
- 참고:
  - services/order-service/src/main/kotlin/com/nextmall/order/presentation/dto/CreateOrderSnapshotRequest.kt:1-22
  - services/order-service/src/main/kotlin/com/nextmall/order/presentation/controller/OrderController.kt:3-34
  - services/product-service/src/main/kotlin/com/nextmall/product/presentation/request/CreateProductRequest.kt:1-22

31) Domain → API 매핑 분리 기준이 약함
- 근거: checkout 응답 매핑이 중앙화되어 있으나 중복이 존재.
- 리스크: API 계약 누락/드리프트 가능.
- 참고:
  - services/checkout-service/src/main/kotlin/com/nextmall/checkout/presentation/response/CheckoutResponseMapper.kt:15-113

32) 레이어 경계 규칙이 명시적으로 고정되어 있지 않음
- 근거: controller/application/domain/infrastructure 경계 규칙을 고정하는 문서 부재.
- 리스크: 향후 구조가 서비스별로 쉽게 달라질 수 있음.
- 참고:
  - docs/architecture/commonization-standards.md:1-68

Notes
- Checkout 컨트롤러가 도메인 모델(Checkout/Order)을 그대로 응답으로 반환. API 안정성 관점에서 재검토 여지 있음.
  - Reference: services/checkout-service/src/main/kotlin/com/nextmall/checkout/presentation/controller/CheckoutController.kt:26-72

Suggested next steps
33) checkout-service에 PassportTokenSecurityConfig 추가 (user/order/product와 정렬).
34) 주문/상품 수정에 소유자/권한 검증 추가.
35) checkout 요청에 @Valid 및 수치 제약 강화.
36) 외부 API에서 통일된 Money/ID 표현 결정.
37) JSON 네이밍 전략 통일 및 문서 반영.
38) DTO/패키지 네이밍 컨벤션 정렬.
39) principal 추출 위치(authentication.principal vs details) 기준 확정.
40) UCP 문서를 단일 기준 + API 상세 분리로 정리.
41) 리뷰 문서 인덱스 유지 규칙 정리.
42) MVP Kafka 이벤트 계약 및 소비자 역할 정의 (ProductCreated → catalog-indexer 캐시 갱신).
