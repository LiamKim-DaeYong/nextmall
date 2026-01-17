# NextMall Progress

## 접근법: E2E 기반 점진적 검증

> 단위 테스트는 통과하지만 실제 동작 여부를 모름.
> QA가 수동으로 하는 것을 코드로 자동화.

**원칙**
1. **시나리오 하나씩 추가** - 한 번에 다 만들지 않음
2. **실패 → 수정 → 통과** - 테스트가 이정표
3. **1차 흐름 완성 → 2차 리팩토링** - 완벽보다 동작 우선

---

## E2E 테스트 파이프라인

```
./gradlew :e2e-test:test
    │
    ├─ 1. Jib으로 서비스 Docker 이미지 빌드
    ├─ 2. Testcontainers로 전체 스택 실행
    │      (PostgreSQL + Redis + Gateway + BFF + Auth + User + ...)
    ├─ 3. Karate 시나리오 순차 실행
    └─ 4. 컨테이너 정리
```

**기술 스택**
| 역할 | 기술 |
|------|------|
| 이미지 빌드 | Jib (Dockerfile 없이 Gradle에서) |
| 컨테이너 실행 | Testcontainers |
| 시나리오 테스트 | Karate (Gherkin 문법) |

---

## 현재 상태

**2026-01 체크포인트**

| 영역 | 상태 |
|------|------|
| 인프라 | Docker Compose (DB, Redis, Kafka) |
| 서비스 | Gateway, BFF, Auth, User, Product, Order 스켈레톤 |
| E2E 환경 | ❌ 미구축 |

**핵심 과제**
- [ ] E2E 테스트 환경 세팅 (Jib + Testcontainers + Karate)
- [ ] 첫 번째 시나리오: 회원가입

---

## 진행 중 (In Progress)

### E2E 테스트 환경 구축

**Phase 1: 기본 세팅**
- [ ] e2e-test Gradle 모듈 생성
- [ ] Karate 의존성 추가
- [ ] 각 서비스에 Jib 플러그인 추가
- [ ] Testcontainers 설정

**Phase 2: 첫 번째 시나리오**
- [ ] 회원가입 테스트 작성
- [ ] 테스트 실행 → 실패 확인
- [ ] 실패 원인 수정
- [ ] 통과

---

## 시나리오 진행 현황

> 하나씩 추가하며 체크

- [ ] 회원가입
- [ ] 로그인
- [ ] 토큰 갱신
- [ ] 내 정보 조회
- [ ] (이후 하나씩 추가)

---

## 완료 (Done)

**인프라/공통**
- Docker Compose (PostgreSQL, Redis, Kafka)
- JWT 인증, PBAC 인가 기반
- 서비스 간 내부 토큰 패턴
- CQRS (JPA + jOOQ)

**서비스 스켈레톤**
- api-gateway (라우팅, 토큰 존재 검증)
- bff-service (오케스트레이션)
- auth-service (로그인, 토큰)
- user-service (회원)
- product-service (상품 CRUD)
- order-service (주문 CRUD)