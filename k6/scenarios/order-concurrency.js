/**
 * Order Concurrency Test
 *
 * 목적: 동시 주문 시 재고 동시성 제어 테스트
 * 측정: 충돌률, 성공률, 최종 재고 정합성
 *
 * 실행:
 *   k6 run k6/scenarios/order-concurrency.js
 *
 * 환경변수:
 *   BASE_URL: API Gateway URL (default: http://localhost:8080/api/v1)
 *   TEST_EMAIL: 테스트 계정 이메일
 *   TEST_PASSWORD: 테스트 계정 비밀번호
 *   TEST_STOCK: 테스트 상품 재고 (default: 100)
 */
import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter, Trend } from 'k6/metrics';
import { config } from '../lib/config.js';
import { getOrCreateTestUser, getAuthHeaders } from '../lib/auth.js';

// 커스텀 메트릭
const orderSuccess = new Counter('order_success');
const orderConflict = new Counter('order_conflict');
const orderInsufficientStock = new Counter('order_insufficient_stock');
const orderOtherError = new Counter('order_other_error');
const orderDuration = new Trend('order_duration');

// 테스트 설정
const TEST_STOCK = Number.isNaN(parseInt(__ENV.TEST_STOCK)) ? 100 : parseInt(__ENV.TEST_STOCK);
const DEBUG_LOG_BODY = __ENV.K6_DEBUG === 'true';

export const options = {
  scenarios: {
    concurrent_orders: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '10s', target: 20 },
        { duration: '30s', target: 50 },
        { duration: '1m', target: 50 },
        { duration: '30s', target: 100 },
        { duration: '1m', target: 100 },
        { duration: '30s', target: 0 },
      ],
    },
  },
  thresholds: {
    http_req_duration: ['p(95)<1000'],
    http_req_failed: ['rate<0.3'],
  },
};

export function setup() {
  console.log(`BASE_URL: ${config.baseUrl}`);
  console.log(`TEST_STOCK: ${TEST_STOCK}`);

  // 테스트 계정 준비
  const user = getOrCreateTestUser();

  // 테스트용 상품 생성 (제한된 재고)
  const timestamp = Date.now();
  const productRes = http.post(
    `${config.baseUrl}/products`,
    JSON.stringify({
      name: `K6 Concurrency Product ${timestamp}`,
      description: 'Concurrency test - limited stock',
      price: 10000,
      stock: TEST_STOCK,
      category: 'TEST',
    }),
    { headers: getAuthHeaders(user.accessToken) }
  );

  if (productRes.status !== 201) {
    console.error(`Product creation failed: ${productRes.status}`);
    if (DEBUG_LOG_BODY) {
      console.error(`Response: ${productRes.body}`);
    }
    throw new Error('Failed to create test product');
  }

  const productId = productRes.json('productId');

  console.log(`Setup complete - productId: ${productId}, stock: ${TEST_STOCK}`);

  return {
    productId: productId,
    accessToken: user.accessToken,
    initialStock: TEST_STOCK,
  };
}

export default function (data) {
  const startTime = Date.now();

  const res = http.post(
    `${config.baseUrl}/orders`,
    JSON.stringify({
      productId: data.productId,
      quantity: 1,
    }),
    { headers: getAuthHeaders(data.accessToken) }
  );

  const duration = Date.now() - startTime;
  orderDuration.add(duration);

  if (res.status === 200 || res.status === 201) {
    orderSuccess.add(1);
  } else if (res.status === 409) {
    const body = res.body ? res.body.toString() : '';
    if (body.includes('INSUFFICIENT') || body.includes('stock')) {
      orderInsufficientStock.add(1);
    } else {
      orderConflict.add(1);
    }
  } else {
    orderOtherError.add(1);
  }

  check(res, {
    'expected response': (r) => [200, 201, 409].includes(r.status),
  });

  sleep(0.05);
}

export function teardown(data) {
  console.log(`
========================================
Concurrency Test Result
========================================
Product ID: ${data.productId}
Initial Stock: ${data.initialStock}

Metrics:
- order_success: 성공한 주문
- order_conflict: 낙관적 락 충돌
- order_insufficient_stock: 재고 부족
- order_other_error: 기타 에러

충돌률 = order_conflict / total_orders
========================================
  `);
}
