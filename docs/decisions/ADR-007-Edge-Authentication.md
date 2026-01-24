# ADR-007: Edge Authentication 및 Internal Token 발급 위치 변경

> 인증 없이 시작하는 플로우(회원가입 등)에서 내부 서비스 보안 위험을 인지. Gateway에서 Passport 발급으로 대응

## 상태

채택됨 (Accepted) - ADR-005 일부 대체

## 구현 현황 (2026-01-24)
- Auth Service: RS256 토큰 발급 및 `/.well-known/jwks.json` 제공
- Gateway: JWKS 기반 Access Token 검증 설정 적용
- Passport(Internal) Token: Gateway 발급, 내부 서비스 소비

## 배경

### 기존 결정 (ADR-005)

BFF에서 사가 오케스트레이션과 함께 토큰 관련 처리를 통합했다:
- Access Token 검증
- Internal Token 발급
- UI 데이터 집계
- 사가 오케스트레이션

#### 선택 이유 (당시)
- "변경 포인트 최소화"
- "초기 단계에서 응집도 우선"
- "WebFlux 성능 우려로 Gateway 검증 회피"

### 배경 정리

#### 1. BFF 책임 과다

현재 BFF가 담당하는 것:
- Access Token 검증
- Internal Token 발급
- UI 데이터 집계
- 사가 오케스트레이션 (ADR-005)

→ 단일 책임 원칙 위반, 코드 복잡도 증가

#### 2. 보안 이슈

- 인증 없이 시작하는 플로우(회원가입)에서 내부 서비스 보안 리스크 인지
- 회원가입: 토큰 없이 BFF 호출 → BFF가 user-service의 상태 변경 API 호출
- user-service의 `activateUser`, `markSignupFailed` 같은 API가 보호되지 않음
- Gateway에서 Passport를 발급하면, 내부 서비스는 Passport 없이 호출 자체가 불가능

### 핵심 인사이트

1. 역할 분리가 필요함
   - Gateway: 인증 (토큰 검증 + Internal Token 발급)
   - BFF: UI 집계
   - Orchestrator: 사가 관리 (필요 시 분리)
2. 사례 참고가 학습 목적에 부합

## 대안 비교

| 구분 | 현재 (BFF 통합) | Gateway 이전 |
|------|----------------|------------------------------|
| **토큰 검증 위치** | BFF | Gateway |
| **Internal Token 발급** | BFF | Gateway |
| **BFF 책임** | 검증 + 발급 + 집계 + 사가 | 집계만 |
| **보안** | 유효하지 않은 토큰이 BFF 도달 | Gateway에서 차단 |
| **역할 분리** | 불명확 | 상대적으로 명확 |

## 결정

Gateway에서 Access Token 검증 및 Internal Token 발급으로 변경한다.

### 핵심 선택 이유

1. 내부 서비스 보안 위험 완화
- 인증 없이 시작하는 플로우에서 내부 서비스가 보호되지 않는 위험
- Gateway에서 Passport 발급 → 내부 서비스는 Passport 없이 호출 불가
- 외부에서 내부 서비스 직접 호출 차단

2. 역할 정리
- Gateway: 인증 전담
- BFF: UI 집계 전담
- 단일 책임 원칙 준수

3. 역할 정리를 통한 책임 분산
- Gateway에 인증 책임 집중
- BFF/Orchestrator는 본연 역할에 집중

## 목표 아키텍처

**구성 요소**

| 영역 | 역할 |
|---|---|
| Client | Access Token 전달 |
| API Gateway | Access Token 검증, Passport Token 발급, 라우팅 |
| BFF | UI 집계 |
| Orchestrator | Saga/Workflow |
| Auth | 토큰 발급 |
| User/Product/Order/Payment | 도메인 서비스 |

**요청 흐름**
1. Client → Gateway: Access Token 전달
2. Gateway → BFF/Orchestrator/Auth: Passport Token 전달
3. BFF/Orchestrator → 도메인 서비스: Passport Token 전달

## 구현 계획

### Phase 1: Gateway 토큰 검증 추가

#### 선행 작업
HMAC(HS256) → RSA(RS256) 변경 (완료)
- JWKS 공개를 위해 RSA 비대칭키로 전환

#### 작업 내용
1. Auth Service에 JWKS 엔드포인트 추가 (`/.well-known/jwks.json`) (완료)
2. Gateway에서 JWKS 기반 JWT 검증 (완료)
3. 기존 JwtPresenceAuthenticationFilter 비활성화

### Phase 2: Gateway에서 Internal Token 발급

1. Gateway에 Internal Token 발급 로직 추가 (완료)
2. Access Token → Internal Token 변환 (완료)
3. BFF에서 토큰 발급 로직 제거 (완료)

### Phase 3: BFF 역할 정리

1. BFF는 Internal Token 소비만
2. UI 집계 로직에 집중
3. 인증 관련 코드 전면 제거

### Phase 4: Orchestrator 분리 (선택)

- 복잡한 Saga 필요 시 별도 서비스로 분리
- ADR-005 결정 유지 가능 (BFF 통합)
- 도메인 안정화 후 분리 검토

## 트레이드오프

### 얻는 것

- 내부 서비스 보안 강화 기대 (Passport 없이 호출 어려움)
- 역할 정리
- 내부 서비스 코드 단순화 기대

### 포기하는 것

- Gateway 복잡도 증가
- RSA 키 관리 필요
- 마이그레이션 작업 필요

### ADR-005와의 관계

ADR-005 "BFF에서 사가 오케스트레이션 통합"은 유지 가능:
- **변경**: 토큰 검증/발급은 Gateway로 이전
- **유지**: 사가 오케스트레이션은 BFF에서 유지 (도메인 안정화 전까지)
- **향후**: 도메인 안정화 시 Orchestrator 분리 검토

## 기존 결정과의 차이

| 항목 | 기존 (ADR-005 시점) | 변경 |
|------|---------------------|------|
| WebFlux 성능 | Gateway 검증 회피 | JWKS 캐싱 |
| BFF 역할 | 통합 (검증 + 발급 + 집계 + 사가) | 분리 (집계 + 사가만) |
| 근거 | 변경 포인트 최소화 | 내부 서비스 보안 + 역할 분리 |

## 참고 자료

### Netflix
- [Edge Authentication and Token-Agnostic Identity Propagation](https://netflixtechblog.com/edge-authentication-and-token-agnostic-identity-propagation-514e47e0b602)
- [User & Device Identity for Microservices @ Netflix Scale](https://www.infoq.com/presentations/netflix-user-identity/)
- [Netflix Conductor](https://github.com/Netflix/conductor)

### Uber
- [Cadence Workflow](https://github.com/uber/cadence)

### AWS
- [Saga Orchestration Pattern](https://docs.aws.amazon.com/prescriptive-guidance/latest/cloud-design-patterns/saga-orchestration.html)

### BFF 패턴
- [Sam Newman - Backends For Frontends](https://samnewman.io/patterns/architectural/bff/)
- [Phil Calçado - The Back-end for Front-end Pattern](https://philcalcado.com/2015/09/18/the_back_end_for_front_end_pattern_bff.html)
