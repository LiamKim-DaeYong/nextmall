# k6 Performance Tests

재고 동시성 제어 성능 개선을 위한 k6 테스트.

## 설치

```bash
# Windows (chocolatey)
choco install k6

# macOS
brew install k6

# Docker
docker pull grafana/k6
```

## 실행

### 사전 준비

1. Docker Compose로 서비스 실행
2. API Gateway가 `localhost:8000`에서 동작 확인

### Baseline 테스트

```bash
# 기본 실행 (새 계정 자동 생성)
k6 run k6/scenarios/order-baseline.js

# 기존 계정 사용
k6 run --env TEST_EMAIL=test@example.com --env TEST_PASSWORD=Test1234! \
  k6/scenarios/order-baseline.js

# 결과 JSON 저장
k6 run --out json=k6/results/baseline.json k6/scenarios/order-baseline.js
```

### 동시성 테스트

```bash
# 기본 실행 (재고 100개)
k6 run k6/scenarios/order-concurrency.js

# 재고 수량 지정
k6 run --env TEST_STOCK=500 k6/scenarios/order-concurrency.js
```

## 환경변수

| 변수 | 설명 | 기본값 |
|------|------|--------|
| `BASE_URL` | API Gateway URL | `http://localhost:8080/api/v1` |
| `TEST_EMAIL` | 테스트 계정 이메일 | 자동 생성 |
| `TEST_PASSWORD` | 테스트 계정 비밀번호 | `Test1234!` |
| `TEST_STOCK` | 테스트 상품 재고 (concurrency) | `100` |

## 테스트 시나리오

### order-baseline.js

기본 성능 측정 (충분한 재고)

- VU: 0 → 10 → 50 → 0
- 재고: 100,000개

### order-concurrency.js

동시성 테스트 (제한된 재고)

- VU: 0 → 20 → 50 → 100 → 0
- 재고: 100개 (환경변수로 조절 가능)

## 커스텀 메트릭

| 메트릭 | 설명 |
|--------|------|
| `order_success` | 성공한 주문 |
| `order_conflict` | 낙관적 락 충돌 (409) |
| `order_insufficient_stock` | 재고 부족 |
| `order_duration` | 주문 응답 시간 (ms) |

## 디렉토리 구조

```text
k6/
├── lib/
│   ├── auth.js       # 인증 헬퍼
│   └── config.js     # 환경 설정
├── scenarios/
│   ├── order-baseline.js
│   └── order-concurrency.js
├── results/          # 테스트 결과
└── README.md
```
