# 아키텍처 발전 과정

## 개요

NextMall 아키텍처는 모듈러 모놀리식에서 시작해 마이크로서비스, Edge Authentication으로 발전해왔습니다.
각 전환에는 실제 문제 경험과 학습이 있었으며, 이 문서는 그 과정을 기록합니다.

---

## 발전 단계

### Phase 1: 모듈러 모놀리식 (초기)

**구조:**
```text
nextmall (단일 애플리케이션)
├── modules/
│   ├── auth/
│   ├── user/
│   └── ...
└── 단일 실행
```

**장점:**
- 간단한 개발 환경
- 단일 배포

**한계:**
- WebFlux Gateway와 MVC 서비스 혼재 불가
- 동일 프로세스에서 다른 웹 프레임워크 충돌

### Phase 2: 마이크로서비스 전환 (ADR-002)

**구조:**
```text
services/ (배포 단위 분리)
├── api-gateway/    (WebFlux)
├── auth-service/   (MVC)
├── user-service/   (MVC)
└── bff-service/    (MVC)
```

**전환 이유:**
- WebFlux Gateway와 MVC 서비스의 프레임워크 충돌 해결
- 독립 배포 단위 확보

**결정:**
- modules 폴더를 services에 통합 (Self-Contained System)
- common/은 인프라 공통만 유지 (비즈니스 로직 X)

> 상세: [ADR-002](../decisions/ADR-002-모듈러-모놀리식에서-마이크로서비스로-전환.md)

### Phase 3: Edge Authentication 도입 (ADR-007)

**이전 구조:**
```text
Client → Gateway (토큰 존재 확인만) → BFF (검증 + 발급) → Services
```

**현재 구조:**
```text
Client → Gateway (검증 + Passport 발급) → BFF/Services (Passport 소비)
```

**전환 이유:**
- 인증 없이 시작하는 플로우(회원가입 등)에서 내부 서비스 보안 문제 발견
- Gateway에서 Passport 발급 → 내부 서비스는 Passport 없이 호출 불가
- BFF 책임 과다 문제 해결

> 상세: [ADR-007](../decisions/ADR-007-Edge-Authentication.md)

---

## 현재 아키텍처

```text
┌────────────────────────────────────────────────────────────────────────┐
│  Client                                                                │
│      │ [Access Token]                                                  │
│      ▼                                                                 │
│  ┌──────────────────────────────────────────────────────────────┐      │
│  │  API Gateway (8080)                                          │      │
│  │  • Access Token validation (JWKS cache, local verify)        │      │
│  │  • Passport Token issuance                                   │      │
│  │  • Routing, rate limiting                                    │      │
│  └──────────────────────────────────────────────────────────────┘      │
│      │ [Passport Token only]                                           │
│      ▼                                                                 │
│  ┌────────────────┐    ┌────────────────┐    ┌─────────────────┐       │
│  │    BFF         │    │    Auth        │    │    User         │       │
│  │   (8082)       │    │   (8081)       │    │   (8083)        │       │
│  │ UI Aggregation │    │ Token Issuance │    │ User Management │       │
│  └────────────────┘    └────────────────┘    └─────────────────┘       │
│      │                                                                 │
│      ▼                                                                 │
│  ┌──────────────────────────────────────────────────────────────┐      │
│  │  Product / Checkout / Order (Core Flow)                      │      │
│  │  Payment / Stock (planned)                                   │      │
│  └──────────────────────────────────────────────────────────────┘      │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 토큰 흐름

### 토큰 종류

| 토큰 | 발급자 | 검증자 | 용도 | 수명 |
|------|--------|--------|------|------|
| Access Token | Auth Service | Gateway | 사용자 인증 | 15분~1시간 |
| Refresh Token | Auth Service | Auth Service | Access Token 갱신 | 7일~30일 |
| Passport Token | Gateway | 모든 내부 서비스 | 서비스 간 신뢰 전파 | 30초 |

### 요청 흐름

```text
[로그인]
Client → Gateway → Auth Service → Access Token 발급

[일반 요청]
Client ─[Access Token]─→ Gateway
                           │
                           ├─ Access Token 검증 (로컬, JWKS 캐싱)
                           ├─ Passport Token 발급 (30초 TTL)
                           │
                           └─[Passport Token]─→ BFF/Services
                                                   │
                                                   └─ Passport 검증 후 비즈니스 로직
```

---

## 향후 진화 로드맵

### Phase 4: BFF WebFlux 전환

**전환 이유:**
- MVC + Coroutines + WebClient 조합에서 ThreadLocal 한계
- RequestContextHolder/SecurityContextHolder가 스레드 전환 후 접근 불가
- Passport Token 전파 실패 문제

**해결 방향:**
- BFF를 WebFlux로 전환
- Reactor Context 기반으로 컨텍스트 자동 전파

**학습 포인트:**
- ThreadLocal은 비동기 흐름에서 취약
- 동기/비동기 혼합 모델의 복잡도

### Phase 5: Kafka 도입 (Choreography)

**구조:**
```text
Gateway → BFF (동기 응답)
             ↓
          Kafka (이벤트)
             ↓
        각 서비스 (비동기 처리)
```

**적용 대상:**
- 동기 응답 필요: BFF/Orchestrator가 직접 처리 (회원가입, 결제)
- 비동기 OK: Kafka Choreography (알림, 통계, 후처리)

**학습 포인트:**
- Orchestration vs Choreography 트레이드오프
- 이벤트 드리븐 아키텍처
- 최종 일관성 (Eventual Consistency)

### Phase 6: 워크플로우 엔진 검토 (Temporal/Cadence)

**검토 시점:**
- 동기 플로우의 보상 로직이 복잡해질 때
- 장시간 실행 워크플로우 필요 시
- 내구성(Durability) 필요 시

**학습 포인트:**
- 워크플로우 엔진의 장단점
- Saga 패턴의 진화
- Uber, Stripe 사례 연구

---

## 하이브리드 최종 목표

```text
┌─────────────────────────────────────────┐
│                Gateway                  │
└─────────────────────────────────────────┘
                    │
        ┌───────────┴───────────┐
        ▼                       ▼
   ┌─────────┐            ┌─────────────┐
   │   BFF   │            │ Orchestrator│
   │ (Query) │            │ (Sync Edge) │
   │ WebFlux │            │   WebFlux   │
   └─────────┘            └─────────────┘
        │                       │
        │                       ▼
        │                 ┌──────────┐
        │                 │  Kafka   │
        │                 └──────────┘
        │                       │
        └───────────┬───────────┘
                    ▼
    ┌───────┬───────┬───────┬───────┐
    │ user  │ auth  │ order │payment│
    └───────┴───────┴───────┴───────┘
```

---

## 핵심 원칙

> **"왜"를 설명할 수 있는 경험 > 처음부터 정답**

각 단계에서 문제를 경험하고, 그 문제를 해결하기 위해 다음 단계로 전환하는 과정이 가장 큰 학습입니다.

---

## 참고 자료

- [Netflix - Edge Authentication](https://netflixtechblog.com/edge-authentication-and-token-agnostic-identity-propagation-514e47e0b602)
- [Netflix - User & Device Identity](https://www.infoq.com/presentations/netflix-user-identity/)
- [Sam Newman - BFF Pattern](https://samnewman.io/patterns/architectural/bff/)
- [AWS - Saga Orchestration](https://docs.aws.amazon.com/prescriptive-guidance/latest/cloud-design-patterns/saga-orchestration.html)
- [Uber - Cadence](https://github.com/uber/cadence)
