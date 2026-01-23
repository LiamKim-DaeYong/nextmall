rootProject.name = "nextmall"

// ──────────────── Services (Execution / Deployment Units) ────────────────
include("services:api-gateway") // 외부 요청 단일 진입점 (인증 위임, 라우팅, 공통 필터)
include("services:auth-service") // 인증 서비스 실행 단위 (Auth Domain 실행 책임)
include("services:bff-service") // BFF 실행 단위 (API Entry Point / Runtime Only)
include("services:checkout-service") // 체크아웃 서비스 실행 단위 (Checkout Capability 책임)
include("services:order-service") // 주문 서비스 실행 단위 (Order Domain 실행 책임)
include("services:product-service") // 상품 서비스 실행 단위 (Product Domain 실행 책임)
include("services:user-service") // 회원 서비스 실행 단위 (User Domain 실행 책임)

// ──────────────── Integration Layer ────────────────
include("common:integration") // 외부 서비스 통신 공통 레이어 (HTTP, Event, Messaging)

// ──────────────── Common Infrastructure ────────────────
include("common:authorization") // 인가 인프라 통합 (AOP, 캐시, 이벤트)
include("common:data") // 데이터 접근 공통 인프라 (JPA, jOOQ)
include("common:exception") // 공통 예외 정의 및 에러 계약
include("common:identifier") // 식별자 생성 및 관리
include("common:kafka") // Kafka 메시징 공통 인프라
include("common:policy") // 정책 기반 인가 (Policy-Based Authorization)
include("common:redis") // Redis 공통 인프라
include("common:security") // 공통 인증 인프라 (Servlet)
include("common:security-core") // 공통 인증 인프라 (Core)
include("common:test-support") // 테스트 공통 환경 및 테스트 유틸
include("common:util") // 범용 유틸리티 (시간, 문자열, JSON)

// ──────────────── E2E Test ────────────────
include("e2e-test") // E2E 테스트 (Karate + Testcontainers)
