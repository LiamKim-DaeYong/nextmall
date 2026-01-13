# NextMall Progress

## 접근법: ATDD (Acceptance Test-Driven Development)

1. **E2E 시나리오가 북극성** - `e2e-test/E2E-SCENARIOS.md`에 완성된 시스템 정의
2. **테스트 실패 = 구현 필요** - 빨간불 → 구현 → 초록불
3. **체크리스트로 진행 상황 트래킹** - 시나리오별 완료 여부 체크

---

## 현재 상태

**2026-01 체크포인트**

| 영역 | 상태 |
|------|------|
| 인증/인가 | JWT, PBAC 기반 구축됨 |
| Product | CRUD 있음, BFF 연결됨 |
| Order | 기본 플로우 있음, BFF 연결됨 |
| 재고 | 차감/복구 로직 없음 |
| 권한 검증 | PBAC 정책 세부 설정 안 됨 |

**핵심 이슈**
- Order API 스펙 불일치 (totalPrice)
- 재고 관리 로직 없음
- 역할별 권한 검증 미완성

---

## 진행 중 (In Progress)

없음

---

## 다음 (Next)

> E2E 시나리오 기준으로 우선순위 선택

1. **E2E 테스트 환경 세팅**
   - Karate + Docker Compose 구성
   - 기본 시나리오 1개 실행 가능하게

2. **3.1 주문 생성 + 재고 차감**
   - Order API 스펙 정합성 맞추기
   - Product.decreaseStock() 구현
   - BFF 오케스트레이션에서 재고 차감 호출

3. **3.3 주문 취소 + 재고 복구**
   - Product.increaseStock() 구현
   - Order 취소 시 재고 복구

---

## 백로그 (Backlog)

> E2E 시나리오 체크리스트 기준으로 관리
> 상세: `e2e-test/E2E-SCENARIOS.md` 하단 참조

**Phase 1: 핵심 플로우**
- 3.1 주문 생성 + 재고 차감
- 3.3 주문 취소 + 재고 복구
- 4.1 재고 부족 시 주문 실패

**Phase 2: 권한 & 역할**
- 5.2 역할 기반 권한 (BUYER/SELLER)
- 5.3 리소스 소유권 검증
- 2.3 상품 수정/삭제 (본인 것만)

**Phase 3: 완성도**
- 1.1 회원가입 역할 구분
- 3.4 주문 상태 변경 플로우
- 4.2 동시 주문 동시성 제어

**Phase 4: 안정성**
- 6.1 입력 유효성 검증
- 6.2 동시성 & 일관성
- 6.3 경계값 테스트

---

## 완료 (Done)

- 인증 (JWT, 로그인, 토큰 갱신)
- 인가 (PBAC 엔진 기반)
- 서비스 간 통신 (내부 토큰 패턴)
- 공통 인프라 (CQRS, Kafka 설정, Redis)
- Product MVP (CRUD + BFF 연결)
- Order MVP (생성/조회/취소 + BFF 연결)

---

## E2E 테스트 구조

```
nextmall/
├── e2e-test/                    # E2E 테스트 (루트 레벨)
│   ├── E2E-SCENARIOS.md         # 목표 시나리오 정의 (북극성)
│   ├── build.gradle.kts
│   └── src/test/resources/
│       └── features/            # Karate 시나리오
├── docker-compose.test.yml
└── common/test-support/         # 단위/통합 테스트 유틸
```

**실행**
```bash
./gradlew e2eTest
```