rootProject.name = "nextmall"

// ──────────────── Application Layer ────────────────
include("api") // 서비스 진입점 (Spring Boot 실행 모듈)

// ──────────────── Domain Modules ────────────────
include("modules:auth") // 인증/인가 도메인 (Authentication & Authorization)
include("modules:user") // 회원 도메인 (User Registration, Profile, Role 등)

// ──────────────── Common Infrastructure ────────────────
include("common:data") // 데이터 코어(JPA, jOOQ 등 공통 설정)
include("common:redis") // Redis 공통 인프라 설정
include("common:util") // 범용 유틸리티(시간, 문자열, JSON 등)
include("common:identifier") // 식별자 생성/관리
include("common:test-support") // 테스트 공통 환경 및 유틸
