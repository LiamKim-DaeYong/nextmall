rootProject.name = "nextmall"

// ──────────────── Services (Execution / Deployment Units) ────────────────
include("services:api-gateway") // 외부 요청 단일 진입점 (인증 위임, 라우팅, 공통 필터)
include("services:auth-service") // 인증 서비스 실행 단위 (Auth Domain 실행 책임)
include("services:bff-service") // BFF 실행 단위 (API Entry Point / Runtime Only)
include("services:checkout-service") // 체크아웃 서비스 실행 단위 (Checkout Capability 책임)
include("services:orchestrator-service") // 오케스트레이션 실행 단위 (Saga / Workflow)
include("services:order-service") // 주문 서비스 실행 단위 (Order Domain 실행 책임)
include("services:product-service") // 상품 서비스 실행 단위 (Product Domain 실행 책임)
include("services:user-service") // 회원 서비스 실행 단위 (User Domain 실행 책임)

// ──────────────── Common Infrastructure ────────────────
include("common:data") // 데이터 접근 공통 인프라 (JPA, jOOQ)
include("common:exception") // 공통 예외 정의 및 에러 계약
include("common:web-core") // Spring 공통 기반 모듈
include("common:web-mvc") // MVC 공통 웹 설정 (ExceptionHandler, WebMvcConfigurer)
include("common:web-reactive") // Reactive 공통 웹 설정 (ExceptionHandler, WebFluxConfigurer)
include("common:identifier") // 식별자 생성 및 관리
include("common:kafka") // Kafka 메시징 공통 인프라
include("common:policy") // 정책 기반 인가 (Policy-Based Authorization)
include("common:redis") // Redis 공통 인프라
include("common:test-support") // 테스트 공통 환경 및 테스트 유틸
include("common:util") // 범용 유틸리티 (시간, 문자열, JSON)

// ──────────────── E2E Test ────────────────
include("e2e-test") // E2E 테스트 (Karate + Testcontainers)
