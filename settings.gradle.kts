rootProject.name = "nextmall"

// ──────────────── Application Entry Layer ────────────────
include("api-gateway") // 외부 요청의 단일 진입점 역할을 하는 API Gateway (인증, 라우팅, 공통 필터)

// ──────────────── Application-Level Modules ────────────────
include("modules:bff") // 여러 도메인 모듈을 조합하는 애플리케이션 레벨 오케스트레이션 레이어 (도메인 로직 없음)

// ──────────────── Domain Modules ────────────────
include("modules:auth") // 인증/인가 도메인 (Authentication & Authorization)
include("modules:user") // 회원 도메인 (회원 가입, 조회, 프로필, 권한 등)

// ──────────────── Integration Layer ────────────────
include("common:integration") // 외부 서비스 통신을 위한 공통 통합 레이어 (HTTP, Event, Messaging 등)

// ──────────────── Common Infrastructure ────────────────
include("common:data") // 데이터 접근 공통 인프라 (JPA, jOOQ 등)
include("common:redis") // Redis 공통 인프라 설정
include("common:util") // 범용 유틸리티 (시간, 문자열, JSON 등)
include("common:identifier") // 식별자 생성 및 관리
include("common:test-support") // 테스트 공통 환경 및 테스트 유틸리티
