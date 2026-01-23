# UCP Order 최소 스키마/엔드포인트 (초안)

이 문서는 UCP 기준으로 Order 기능을 최소 구현하기 위한 API 계약 초안이다.
Checkout 서비스는 별도로 존재한다고 가정하며, Order는 Checkout 완료 결과를
UCP Order 엔티티로 확정/보관/조회하는 역할을 가진다.

참고: UCP 프로토콜 버전 2026-01-11 기준.

## 1) 리소스

- Order: checkout 완료 후 확정된 주문

## 2) 상태/이벤트 (최소)

Order는 불변 스냅샷(line_items/totals)을 가진다.
상태 변화는 fulfillment/adjustments 이벤트로 누적한다.

필수 이벤트
- Order created (반드시 발행)

## 3) 공통 규칙

- 날짜는 RFC 3339
- 금액은 최소 단위(센트 등)
- line_items 및 totals는 checkout 시점 값 복사
- Order 응답은 항상 UCP 메타(ucp.version, capabilities) 포함
- 웹훅은 Request-Signature 헤더로 서명(detached JWT)

## 4) 엔드포인트 (REST)

### 4.1 Get Order
- GET /v1/ucp/orders/{order_id}
- Response
  - order 객체

### 4.2 Create Order (Checkout 완료용)
- POST /v1/ucp/orders
- Request (최소)
  - checkout_id (필수)
  - line_items[] (필수)
  - totals (필수)
  - currency (필수)
  - permalink_url (옵션)
- Response
  - order 객체

## 5) Order 스키마 (최소)

```json
{
  "id": "ord_123",
  "checkout_id": "chk_123",
  "permalink_url": "https://example.com/order/ord_123",
  "line_items": [
    {
      "id": "li_001",
      "title": "Basic Hoodie",
      "quantity": 1,
      "price": { "amount": 4900, "currency": "USD" },
      "image_url": "https://example.com/item.png"
    }
  ],
  "fulfillment": {
    "expectations": [],
    "events": []
  },
  "adjustments": [],
  "totals": {
    "subtotal": { "amount": 4900, "currency": "USD" },
    "tax": { "amount": 0, "currency": "USD" },
    "shipping": { "amount": 0, "currency": "USD" },
    "discount": { "amount": 0, "currency": "USD" },
    "total": { "amount": 4900, "currency": "USD" }
  }
}
```

## 6) Order 이벤트 전송 (Webhook)

- Order created 이벤트는 반드시 발행
- 이벤트 payload는 전체 Order 엔티티 포함
- 플랫폼이 제공한 webhook URL로 전송
- 요청 헤더: Request-Signature (detached JWT)
- 실패 시 재시도

## 7) 구현 범위 (MVP)

- Checkout 완료 시 Order 생성(POST /v1/ucp/orders)
- Order 조회(GET /v1/ucp/orders/{order_id})
- Order created 웹훅 발행
- fulfillment/adjustments는 빈 리스트로 시작

## 8) 미정/결정 필요

- webhook 서명 키 관리 주체(order-service vs gateway)
- permalink_url 생성 규칙
- 이벤트 재시도 정책(백오프, 최대 횟수)
- order_id 포맷(숫자 vs prefixed string)
