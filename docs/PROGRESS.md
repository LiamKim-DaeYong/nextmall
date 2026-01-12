# NextMall Progress

## 현재 상태

**2026-01 체크포인트**

| 영역 | 상태 |
|------|------|
| Product | CRUD 완료, stock 필드 있음 |
| Order | 생성/조회/취소 있음, 가격 하드코딩 |
| BFF | Auth/User만 연결됨 |
| 서비스 간 통신 | Product/Order 클라이언트 없음 |

**핵심 이슈**: BFF에서 Product/Order 서비스 호출 못함 → 주문 플로우 E2E 불가

---

## 진행 중 (In Progress)

없음

---

## 다음 (Next)

1. **BFF ↔ Product 클라이언트 연결**
   - ProductServiceClient 생성
   - 기존 패턴(WebClientUserServiceClient) 따라가기

2. **BFF ↔ Order 클라이언트 연결**
   - OrderServiceClient 생성

3. **주문 생성 플로우 연결**
   - BFF에서 Product 가격 조회 → Order 생성
   - 하드코딩된 가격 제거

---

## 백로그 (Backlog)

**단기**
- 재고 차감 로직 (Product.decreaseStock)
- 주문 생성 시 재고 차감 연동
- Saga 보상 트랜잭션 (간단한 try-catch부터)

**중기**
- PBAC 정책 확장 (판매자는 자기 상품만)
- Kafka 이벤트 발행 (주문 생성 이벤트)
- 분산 락 (재고 동시성)

**후순위**
- API 문서화 (Swagger)
- 성능 테스트 (k6)
- Circuit Breaker
- 모니터링 (Micrometer)

---

## 완료 (Done)

- 인증 (JWT, 로그인, 토큰 갱신)
- 인가 (PBAC, 정책 엔진 기반)
- 서비스 간 통신 (내부 토큰 패턴)
- 공통 인프라 (CQRS, Kafka 설정, Redis)
- Product MVP (CRUD)
- Order MVP (생성/조회/취소)