# NextMall AI Context

이 문서는 AI 어시스턴트가 프로젝트를 빠르게 이해하기 위한 컨텍스트입니다.
사람도 읽을 수 있지만, 주 독자는 AI입니다.

---

## 프로젝트 개요

- 이커머스 플랫폼 (학습 목적, 실무 베스트 프랙티스 지향)
- 1인 개발, 단일 Gradle 멀티모듈 프로젝트
- Spring Boot 4.0.1 / Kotlin / PostgreSQL / Redis / Kafka

---

## 서비스 통신 구조

```text
Client → Gateway(8080) → BFF(8082) → Domain Services
                                  └→ Orchestrator(8087) → Domain Services

읽기: BFF → Domain Service 직접 호출
쓰기: BFF → Orchestrator → Domain Services (사가 패턴)
이벤트: Order → Kafka(order.created) → Product(재고 차감)
인증: Gateway에서 Access Token → Passport Token 변환 (ADR-005)
```

---

## 서비스별 역할

| 서비스 | 포트 | 프레임워크 | 역할 |
|--------|------|-----------|------|
| api-gateway | 8080 | WebFlux | 라우팅, Access Token 검증, Passport Token 발급 |
| bff-service | 8082 | WebFlux | 프론트엔드 진입점, 읽기 직접 호출 / 쓰기 Orchestrator 위임 |
| orchestrator-service | 8087 | WebFlux | 주문 생성 사가, 회원가입 사가 (보상 트랜잭션 포함) |
| auth-service | 8081 | MVC | 인증 토큰 발급/갱신, 로그인 Strategy 패턴, Redis에 RefreshToken 저장 |
| user-service | 8083 | MVC | 사용자 CRUD, 상태 전이 (PENDING → ACTIVE) |
| product-service | 8084 | MVC | 상품 CRUD, 재고 관리 (낙관적 락 @Version), Kafka Consumer로 재고 차감 |
| order-service | 8085 | MVC | 주문 생성, JSON 컬럼 저장, Kafka로 order.created 이벤트 발행 |
| checkout-service | - | MVC | 체크아웃 라이프사이클, 금액 계산 (subtotal/tax/shipping/discount) |

---

## 공통 모듈 (common/)

| 모듈 | 역할 |
|------|------|
| data | JPA + jOOQ 데이터 접근 기반 |
| exception | 에러 응답 계약 |
| web-core | Spring Security 공통 (JWT 컨버터 등) |
| web-mvc | MVC 서비스용 예외 핸들러, 설정 |
| web-reactive | WebFlux 서비스용 예외 핸들러, 설정 |
| identifier | Snowflake ID 생성기 |
| kafka | Kafka 메시징 공통 |
| policy | PBAC 인가 (Policy-Based Access Control) |
| redis | Redis 인프라 (분산 락, 캐시, Lua 스크립트) |
| test-support | 테스트 인프라 (Testcontainers, 어노테이션, 보안 스텁) |
| util | 시간, 문자열, JSON 유틸리티 |

---

## 코드 패턴

- **CQRS**: JPA(쓰기) + jOOQ(읽기) — ADR-001
- **인가**: PBAC, common:policy 모듈 — ADR-003
- **인증**: Edge Authentication, Gateway에서 Passport Token 발급 — ADR-005
- **ID 생성**: Snowflake, common:identifier 모듈
- **예외**: 서비스별 `ErrorCode` enum + `GlobalExceptionHandler`
- **모듈 의존성**: 단방향 원칙 — ADR-004
- **이벤트**: Envelope + Payload 구조, At-least-once, eventId 기반 멱등성

---

## 테스트 인프라

- **프레임워크**: Kotest FunSpec + MockK + SpringMockK
- **컨테이너**: Testcontainers (PostgreSQL, Redis) — 싱글턴 + reuse
- **어노테이션**:
  - `@IntegrationTest` — 전체 컨텍스트 + PostgreSQL + Redis
  - `@ControllerTest` — MVC 슬라이스
  - `@RepositoryTest` — JPA 슬라이스
  - `@RedisIntegrationTest` — Redis 슬라이스
- **보안 스텁**: `TestPassportTokenIssuer` (JWT 토큰 생성), `NoOpEventPublisher` (Kafka 스텁)
- **미구현**: Kafka 테스트 컨테이너, 테스트 데이터 빌더
- **서비스별 픽스처**: 각 서비스의 `src/test/kotlin/.../fixture/`에 위치

---

## 핵심 비즈니스 흐름

### 주문 생성 (가장 중요한 흐름)
1. BFF → Orchestrator: 주문 요청
2. Orchestrator → Product: 상품 조회 (HTTP)
3. Orchestrator → Redis: 재고 예약 (Lua 스크립트)
4. Orchestrator → Order: 주문 생성 (HTTP)
5. Order → Kafka: `order.created` 이벤트 발행
6. Product ← Kafka: 재고 차감 (Consumer)
7. 실패 시: Orchestrator가 Redis 재고 롤백 (보상)

### 회원가입
1. BFF → Orchestrator: 가입 요청
2. Orchestrator → User: 사용자 생성 (PENDING)
3. Orchestrator → Auth: 인증 계정 생성
4. Orchestrator → User: 사용자 활성화 (ACTIVE)
5. 실패 시: 보상 트랜잭션

---

## 현재 작업 상태

- [진행중] 테스트 코드 보강 시작 단계
- [보류] 낙관적 락 기반 재고 동시성 제어 (perf/phase-1-optimistic-lock) — 테스트 이후 재개
- [완료] OpenTelemetry + Grafana 모니터링, springdoc-openapi 문서화

---

## 상세 문서 참조

- 아키텍처: `docs/architecture/`
- 기술 결정: `docs/decisions/ADR-*.md`
- TODO/로드맵: `docs/TODO.md`
- API 흐름 예시: `http/flows/`
- 운영 가이드: `docs/operations/`
