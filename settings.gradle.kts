rootProject.name = "nextmall"

// ──────────────── Service (Execution / Deployment Unit) ────────────────
include("services:api-gateway") // 외부 요청의 단일 진입점 (인증 위임, 라우팅, 공통 필터)
include("services:auth-service") // 인증 서비스 실행 단위 (Auth Domain 실행 책임)
include("services:user-service") // 회원 서비스 실행 단위 (User Domain 실행 책임)
include("services:bff-service") // BFF 실행 단위 (API Entry Point / Runtime Only)

// ──────────────── Application-Level Modules ────────────────
include("modules:bff") // 여러 도메인을 조합하는 애플리케이션 오케스트레이션 레이어

// ──────────────── Domain Modules ────────────────
include("modules:auth") // 인증/인가 도메인 (Authentication & Authorization)
include("modules:user") // 회원 도메인 (회원 가입, 조회, 프로필, 권한 등)

// ──────────────── Integration Layer ────────────────
include("common:integration") // 외부 서비스 통신 공통 레이어 (HTTP, Event, Messaging)

// ──────────────── Common Infrastructure ────────────────
include("common:data") // 데이터 접근 공통 인프라 (JPA, jOOQ)
include("common:exception") // 공통 예외 정의 및 에러 계약
include("common:identifier") // 식별자 생성 및 관리
include("common:redis") // Redis 공통 인프라
include("common:util") // 범용 유틸리티 (시간, 문자열, JSON)
include("common:security") // 공통 인증 인프라 (AuthN only)
include("common:test-support") // 테스트 공통 환경 및 테스트 유틸
