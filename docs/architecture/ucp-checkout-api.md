# UCP Checkout 최소 스키마/엔드포인트 (초안)

이 문서는 UCP 기준으로 Checkout 기능을 최소 구현하기 위한 API 계약 초안이다.
Catalog/Cart는 내부 도메인으로 두고, Checkout의 line_items에 매핑한다.

## 1) 리소스

- Checkout: 장바구니와 결제 직전까지의 세션

## 2) 상태 모델 (최소)

- incomplete -> ready_for_complete -> complete_in_progress -> completed
- requires_escalation (사용자 UI 필요)
- canceled

## 3) 공통 규칙

- 날짜는 RFC 3339
- 금액은 최소 단위(센트 등)
- 결제 관련 정보는 payment.handlers 기반
- errors/messages는 표준 오류 구조를 따른다

## 4) 엔드포인트 (REST)

### 4.1 Create Checkout
- POST /v1/ucp/checkout (Gateway/BFF)
- POST /checkouts (Checkout Service 내부용)
- Request (최소)
  - line_items[]
  - currency
  - buyer (옵션)
  - return_url (옵션)
  - cancel_url (옵션)
- Response
  - checkout 객체

### 4.2 Get Checkout
- GET /v1/ucp/checkout/{checkout_id} (Gateway/BFF)
- GET /checkouts/{checkout_id} (Checkout Service 내부용)
- Response
  - checkout 객체

### 4.3 Update Checkout
- PATCH /v1/ucp/checkout/{checkout_id} (Gateway/BFF)
- PATCH /checkouts/{checkout_id} (Checkout Service 내부용)
- Request
  - line_items[] (옵션)
  - buyer (옵션)
  - shipping_address (옵션)
  - billing_address (옵션)
- Response
  - checkout 객체

### 4.4 Complete Checkout
- POST /v1/ucp/checkout/{checkout_id}/complete (Gateway/BFF)
- POST /checkouts/{checkout_id}/complete (Checkout Service 내부용)
- Request
  - payment (필수)
  - confirm (옵션)
- Response
  - order 객체 (또는 checkout + order_ref)

### 4.5 Cancel Checkout
- POST /v1/ucp/checkout/{checkout_id}/cancel (Gateway/BFF)
- POST /checkouts/{checkout_id}/cancel (Checkout Service 내부용)
- Response
  - checkout 객체

## 5) Checkout 스키마 (최소)

```json
{
  "id": "chk_123",
  "status": "incomplete",
  "currency": "USD",
  "line_items": [
    {
      "id": "li_001",
      "title": "Basic Hoodie",
      "quantity": 1,
      "price": { "amount": 4900, "currency": "USD" },
      "image_url": "https://example.com/item.png"
    }
  ],
  "totals": {
    "subtotal": { "amount": 4900, "currency": "USD" },
    "tax": { "amount": 0, "currency": "USD" },
    "shipping": { "amount": 0, "currency": "USD" },
    "discount": { "amount": 0, "currency": "USD" },
    "total": { "amount": 4900, "currency": "USD" }
  },
  "messages": [],
  "links": {
    "terms": "https://example.com/terms",
    "privacy": "https://example.com/privacy",
    "refund": "https://example.com/refund"
  },
  "expires_at": "2026-01-23T12:00:00Z",
  "payment": {
    "handlers": [
      { "type": "card", "provider": "internal" }
    ]
  }
}
```

## 6) Order 스키마 (Complete 응답 최소형)

```json
{
  "id": "ord_123",
  "checkout_id": "chk_123",
  "permalink_url": "https://example.com/order/ord_123",
  "line_items": [ /* checkout line_items 복사 */ ],
  "fulfillment": {
    "expectations": [],
    "events": []
  },
  "adjustments": [],
  "totals": { /* checkout totals 복사 */ }
}
```

## 7) 구현 범위 (MVP)

- Product/Catalog은 Mock 가능
- Checkout/Order 흐름을 UCP 구조로 먼저 고정
- 결제 핸들러는 내부 mock 처리 가능
