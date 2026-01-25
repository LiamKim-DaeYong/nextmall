# NextMall Architecture Review - 2026-01-24

## Scope

- 프로젝트 전반의 아키텍처 구조 검토
- 서비스 간 의존 관계 및 책임 분리 분석
- 디테일한 코드 품질보다 아키텍처링 관점에 집중
- 권장 가이드라인 제시

---

## 1. 전체 구조 요약

### 1.1 모듈 구성

```text
nextmall/
├── services/              # 배포 단위 (독립 실행)
│   ├── api-gateway/       # WebFlux - 진입점 (8080)
│   ├── auth-service/      # MVC - 인증/토큰 발급 (8081)
│   ├── bff-service/       # WebFlux - UI 집계 (8082)
│   ├── user-service/      # MVC - 회원 관리 (8083)
│   ├── product-service/   # MVC - 상품 관리 (8084)
│   ├── order-service/     # MVC - 주문 관리 (8085)
│   ├── checkout-service/  # MVC - 결제 처리 (8086)
│   └── orchestrator-service/ # WebFlux - 사가 조율 (8087)
│
├── common/                # 인프라 공통 모듈 (14개)
│   ├── exception/         # 공통 예외 정의
│   ├── identifier/        # 식별자 생성
│   ├── security-core/     # JWT 검증 (WebFlux 호환)
│   ├── security/          # Spring Security (Servlet)
│   ├── data/              # JPA, jOOQ, Liquibase
│   ├── util/              # 범용 유틸리티
│   ├── policy/            # 정책 기반 인가 정의
│   ├── authorization/     # 인가 인프라 (AOP)
│   ├── redis/             # 세션, 캐시, 분산 락
│   ├── kafka/             # 메시징 공통
│   ├── integration/       # HTTP 클라이언트
│   └── test-support/      # 테스트 인프라
│
└── e2e-test/              # E2E 테스트 (Karate)
```

### 1.2 의존성 계층

```text
[Tier 4] services/*
    ↑
[Tier 3] common/authorization, common/integration, common/test-support
    ↑
[Tier 2] common/security, common/security-core, common/data, common/redis, common/kafka
    ↑
[Tier 1] common/exception, common/util, common/policy, common/identifier
```

**원칙**: 의존성은 위에서 아래로만 흐름 (ADR-004 준수)

---

## 2. 아키텍처 평가

### 2.1 잘 되어 있는 부분

| 영역 | 평가 | 비고 |
|------|------|------|
| **의존성 방향** | ✅ 우수 | 순환 의존성 없음, ADR-004 준수 |
| **Edge Authentication** | ✅ 우수 | Gateway에서 검증 → Passport 발급 패턴 |
| **CQRS 분리** | ✅ 우수 | JPA(Command) + jOOQ(Query) 일관 적용 |
| **ADR 문서화** | ✅ 우수 | 현재 ADR을 통해 의사결정 추적 가능 |
| **기술 스택 선택** | ✅ 적절 | WebFlux(Gateway/BFF), MVC(Domain) 합리적 |

### 2.2 구조적 이슈

#### Issue 1: BFF vs Orchestrator 책임 모호

**현황**:
- BFF (8082): 회원가입/로그인 사가, UI 집계
- Orchestrator (8087): 사가 오케스트레이션

**문제점**:
- 두 서비스가 동일한 패턴 사용 (`common/integration` 통한 서비스 호출)
- 라우팅/보안 책임 재정의가 문서로 충분히 정리되지 않음
- 어떤 워크플로우가 어디에 속하는지 기준 불명확

**영향**: 새로운 워크플로우 추가 시 배치 기준 모호

**권장**:
```text
BFF = UI 집계 전용 (데이터 조합, 포맷팅)
Orchestrator = 비즈니스 워크플로우 전용 (사가, 트랜잭션 조율)
```

---

#### Issue 2: 문서와 실제 구조 불일치

**현황**:
문서에 언급된 `modules/` 폴더가 settings.gradle.kts에 없음

```kotlin
// 문서 설계
modules/  // 비즈니스 모듈 재사용

// 실제 구조
// modules/ 없음 - services/와 common/만 존재
```

**영향**: 문서와 실제 구조 간 혼란 유발

**권장**: 문서를 최신 구조로 정리

---

#### Issue 3: Domain Services 의존성 동일 패턴

**현황**:
User, Product, Order, Checkout 서비스가 완전히 동일한 의존성 보유

```kotlin
// 모든 Domain Service가 동일
implementation(project(":common:authorization"))
implementation(project(":common:data"))
implementation(project(":common:exception"))
implementation(project(":common:identifier"))
implementation(project(":common:security"))
implementation(project(":common:util"))
```

**문제점**:
- 실제로 모든 서비스에 authorization이 필요한가?
- 단순 조회 전용 서비스는 불필요한 의존성 가질 수 있음
- Copy-Paste 방식으로 확장 시 기술 부채 누적

**권장**: 서비스별 기본 의존성 분석 후 최소화

---

#### Issue 4: security vs security-core 구분 기준 미문서화

**현황**:
- `security-core`: WebFlux 호환 (Gateway, BFF, Orchestrator 사용)
- `security`: Servlet 기반 (Domain Services 사용)

**문제점**: 이 구분 기준이 문서에 명시되어 있지 않음

**권장**: `docs/architecture/commonization-standards.md`에 보안 모듈 선택 기준 추가

---

#### Issue 5: test-support 복잡성

**현황**:
```kotlin
// common/test-support의 의존성
implementation(project(":common:identifier"))
implementation(project(":common:kafka"))
implementation(project(":common:redis"))
implementation(project(":common:security"))
```

**문제점**:
- 테스트 모듈이 4개의 인프라 모듈에 의존
- 특정 테스트에 불필요한 의존성까지 끌려옴
- ADR-004에서도 "향후 분리 검토" 언급

**권장**: 도메인별 test-support 분리 검토 (e.g., test-support-data, test-support-messaging)

---

## 3. 빠진 구성 요소

| 구성 요소 | 현재 상태 | 우선순위 | 비고 |
|----------|----------|---------|------|
| **Kafka 이벤트 적용** | 인프라만 준비 | 높음 | common/kafka 있으나 도메인 미적용 |
| **Circuit Breaker** | 문서만 존재 | 중간 | circuit-breaker.md만, Resilience4j 미적용 |
| **Rate Limiting** | 없음 | 중간 | Gateway에 미구현 |
| **Distributed Tracing** | 기본 설정만 | 중간 | OTEL 설정 있으나 연동 미완 |
| **API Versioning 전략** | /api/v1/ 만 | 낮음 | 호환성 관리 전략 미정의 |

---

## 4. 권장 가이드라인

### 4.1 서비스 책임 분리 원칙

```text
┌─────────────────────────────────────────────────────────────┐
│                      API Gateway                            │
│  - External token verification                              │
│  - Passport token issuance                                  │
│  - Routing                                                  │
│  - Rate Limiting (future)                                   │
└─────────────────────────────────────────────────────────────┘
                              │
          ┌───────────────────┼───────────────────┐
          ▼                   ▼                   ▼
┌──────────────────┐  ┌─────────────────┐  ┌─────────────────┐
│       BFF        │  │   Orchestrator  │  │  Domain Service │
│                  │  │                 │  │                 │
│ - UI aggregation │  │ - Saga pattern  │  │ - Single domain │
│ - Response fmt   │  │ - Dist. txn     │  │ - CRUD ops      │
│ - Cache merge    │  │ - Compensation  │  │ - Biz rules     │
└──────────────────┘  └─────────────────┘  └─────────────────┘
```

**원칙**:
1. BFF는 **읽기 중심** - 여러 서비스 데이터를 조합하여 UI에 최적화된 응답 반환
2. Orchestrator는 **쓰기 중심** - 여러 서비스에 걸친 상태 변경 조율
3. Domain Service는 **단일 책임** - 자신의 도메인 데이터만 관리

### 4.2 새 서비스 추가 체크리스트

```markdown
## 새 서비스 추가 시 확인 사항

### 1. 서비스 유형 결정
- [ ] WebFlux 필요? (Gateway, BFF, Orchestrator 패턴)
- [ ] MVC로 충분? (Domain Service)

### 2. 기본 의존성 선택
- [ ] common/exception (기본)
- [ ] common/security 또는 common/security-core (택1)
- [ ] common/data (JPA/jOOQ 필요 시)
- [ ] common/authorization (인가 필요 시)
- [ ] common/integration (서비스 간 호출 시)

### 3. 설정 파일
- [ ] application.yml 포트 할당 (기존 서비스와 충돌 확인)
- [ ] Passport 토큰 검증 설정
- [ ] DB 연결 설정 (필요 시)

### 4. 보안 설정
- [ ] SecurityConfig 추가 (PassportTokenSecurityConfig 임포트)
- [ ] 인증 제외 경로 정의 (actuator/health 등)

### 5. 문서 업데이트
- [ ] settings.gradle.kts에 모듈 추가
- [ ] docs/README.md 업데이트
- [ ] 포트 매핑 문서 업데이트
```

### 4.3 의존성 관리 원칙

```text
## DO (권장)
- 필요한 common 모듈만 선택적으로 의존
- api() 대신 implementation()으로 전이 의존성 최소화
- 테스트 의존성은 testImplementation으로 분리

## DON'T (피하기)
- common 모듈 간 순환 의존성 생성
- services에서 다른 services 직접 의존
- Domain Service에서 WebFlux 의존성 추가
```

### 4.4 보안 모듈 선택 기준

| 상황 | 선택 | 이유 |
|------|------|------|
| WebFlux 기반 서비스 (Gateway, BFF, Orchestrator) | `security-core` | Reactor 호환 필요 |
| MVC 기반 서비스 (Domain Services) | `security` | Servlet 기반 필터 체인 |
| 인가가 필요한 서비스 | + `authorization` | PBAC AOP 적용 |

### 4.5 워크플로우 배치 기준

워크플로우 배치는 별도 문서로 정리한다.
- [워크플로우 아키텍처](../architecture/workflow-architecture.md)

---

## 5. 권장 액션 로드맵

### Phase 1: 즉시 (구조 정리)

| 액션 | 담당 | 산출물 |
|------|------|--------|
| BFF/Orchestrator 책임 명확화 | 설계 | 최신 문서에 정리 |
| 문서 현행화 | 문서 | 최신 구조 반영 |
| 보안 모듈 선택 기준 문서화 | 문서 | commonization-standards.md 추가 |

### Phase 2: 단기 (인프라 완성)

| 액션 | 담당 | 산출물 |
|------|------|--------|
| Kafka 도메인 이벤트 적용 | 개발 | 최소 1개 이벤트 (e.g., OrderCreated) |
| Circuit Breaker 구현 | 개발 | BFF/Orchestrator에 Resilience4j 적용 |
| Rate Limiting 추가 | 개발 | Gateway에 기본 제한 설정 |

### Phase 3: 중기 (안정화)

| 액션 | 담당 | 산출물 |
|------|------|--------|
| 서비스별 의존성 최적화 | 개발 | 불필요 의존성 제거 |
| test-support 분리 검토 | 설계 | ADR 작성 후 결정 |
| API Versioning 전략 수립 | 설계 | ADR 작성 |

---

## 6. 서비스 의존관계 다이어그램

```text
                    ┌──────────────┐
                    │   Client     │
                    └──────┬───────┘
                           │
                    ┌──────▼───────┐
                    │ API Gateway  │ ← Auth Service (JWKS)
                    │    (8080)    │
                    └──────┬───────┘
                           │ Passport Token
           ┌───────────────┼───────────────┐
           │               │               │
    ┌──────▼──────┐ ┌──────▼──────┐ ┌──────▼──────┐
    │     BFF     │ │ Orchestrator│ │   Direct    │
    │   (8082)    │ │   (8087)    │ │   Access    │
    └──────┬──────┘ └──────┬──────┘ └──────┬──────┘
           │               │               │
           └───────────────┼───────────────┘
                           │
       ┌────────────┬──────┴─────┬────────────┐
       │            │            │            │
   ┌───▼────┐  ┌────▼────┐   ┌───▼────┐  ┌────▼─────┐
   │  User  │  │ Product │   │ Order  │  │ Checkout │
   │ (8083) │  │ (8084)  │   │ (8085) │  │  (8086)  │
   └───┬────┘  └────┬────┘   └───┬────┘  └────┬─────┘
       │            │            │            │
       └────────────┴─────┬──────┴────────────┘
                          │
                 ┌────────▼────────┐
                 │   PostgreSQL    │
                 │     + Redis     │
                 └─────────────────┘
```

---

## Appendix: 참고 문서

- [ADR-004 모듈 의존성 원칙](../decisions/ADR-004-모듈-의존성-원칙.md)
- [ADR-005 Edge Authentication](../decisions/ADR-005-Edge-Authentication.md)
- [공통화 및 컨벤션 기준](../architecture/commonization-standards.md)


