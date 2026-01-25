# 이벤트 스키마 기준 (초안)

본 문서는 Kafka 기반 이벤트 설계의 최소 기준을 정리한다.
포트폴리오/학습 목적을 고려해 최소 범위로 시작하되, 설계 의도와 확장성을 드러내는 것을 목표로 한다.

---

## 1. 목표
- 서비스 간 비동기 계약을 명확히 정리한다
- 이벤트 스키마 버전/호환성 규칙을 최소한으로 확보한다
- 재처리/중복 처리(아이덤포턴시) 설계를 문서로 고정한다

## 2. 범위
- 단일 이벤트 흐름(생산자 1개, 소비자 1개)
- 최소 1회 전달(At-least-once) 전송을 기본 전제로 한다
- 스키마 레지스트리는 사용하지 않는다 (향후 고려)

## 3. 이벤트 공통 규칙
- 이벤트의 기본 구조는 봉투(Envelope) + 페이로드(Payload)로 둔다
- 모든 이벤트는 고유 eventId를 두는 것을 기본으로 한다 (UUID 권장)
- 발생 시각 occurredAt은 RFC 3339 UTC로 기록하는 것을 기본으로 한다
- 이벤트 타입은 명시적 문자열로 관리하도록 한다
- 이벤트는 하위 호환 변경만 허용하는 방향으로 둔다 (새 필드 추가만 허용)

## 4. 봉투(Envelope) 정의 (공통)
- eventId: string (UUID)
- eventType: string (예: "ProductCreated")
- version: string (예: "v1")
- occurredAt: string (RFC 3339, UTC)
- producer: string (발행 서비스 식별자)
- traceId: string? (선택, 추적용)

## 5. 토픽 네이밍 규칙
- 포맷: `nextmall.{domain}.{event}.v{major}`
- 예시: `nextmall.product.created.v1`

## 6. 이벤트 예시 (ProductCreated)
```json
{
  "eventId": "0f3c6f6a-3f42-4d9e-8db8-0b0b0a4b0f8a",
  "eventType": "ProductCreated",
  "version": "v1",
  "occurredAt": "2026-01-24T12:00:00Z",
  "producer": "product-service",
  "traceId": "c0a80123-4b2f",
  "payload": {
    "productId": 123,
    "name": "Basic Hoodie",
    "price": { "amount": 4900, "currency": "USD" },
    "stock": 100,
    "category": "tops"
  }
}
```

## 7. 소비자 처리 규칙 (최소)
- eventId 기반 중복 방지 테이블 또는 캐시 사용
- 동일 eventId는 무시하도록 한다 (아이덤포턴트)
- 실패 시 재처리는 재시도 정책에 따른다 (예: 최대 3회)

## 8. 재처리 / 실패 정책 (최소)
- 생산자는 전송 실패 시 제한된 재시도만 수행한다 (1~2회 권장)
- 소비자는 처리 실패 시 재시도 또는 DLQ로 이동한다 (선택)
- 재시도/타임아웃 정책은 서킷브레이커 기준과 정합되게 설정한다
- DLQ 사용 여부는 Phase 2에서 결정한다

## 9. 서킷브레이커 정합성
- 이벤트 전송/처리의 외부 I/O에는 서킷브레이커 정책을 적용한다
- 정책 값은 `circuit-breaker.md` 기준을 참고한다

## 10. 버전 정책
- v1 → v1.x: 필드 추가만 허용 (삭제/변경 금지)
- 하위 호환이 깨지는 변경은 v2 토픽으로 분리

## 11. 향후 고려사항
- 스키마 레지스트리 도입 여부
- 아웃박스(Outbox) 패턴 적용 여부
- 이벤트 서명 또는 무결성 검증 추가

---

## MVP 이벤트 선정 (현재 기준)
- 1차 이벤트: `ProductCreated`
- 이유: product 모듈 작업 진행 중이며, 단일 Producer/Consumer 데모를 가장 빠르게 구성 가능
- 토픽: `nextmall.product.created.v1`
- 소비자 역할(최소): `catalog-indexer` (Redis 기반 상품 조회 캐시 갱신)
