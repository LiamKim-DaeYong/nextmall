/**
 * Order Baseline Test
 *
 * 목적: 현재 주문 시스템의 기본 성능 측정
 * 측정: TPS, 응답시간, 에러율
 *
 * 실행:
 *   k6 run k6/scenarios/order-baseline.js
 *
 * 환경변수:
 *   BASE_URL: API Gateway URL (default: http://localhost:8080/api/v1)
 *   TEST_EMAIL: 테스트 계정 이메일
 *   TEST_PASSWORD: 테스트 계정 비밀번호
 */
import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter, Trend } from 'k6/metrics';
import { config } from '../lib/config.js';
import { getOrCreateTestUser, getAuthHeaders } from '../lib/auth.js';

// 커스텀 메트릭
const orderSuccess = new Counter('order_success');
const orderFailed = new Counter('order_failed');
const orderDuration = new Trend('order_duration');

export const options = {
  scenarios: {
    baseline: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '30s', target: 10 },
        { duration: '1m', target: 50 },
        { duration: '30s', target: 0 },
      ],
    },
  },
  thresholds: {
    http_req_duration: ['p(95)<500'],
    http_req_failed: ['rate<0.05'],
  },
};

export function setup() {
  console.log(`BASE_URL: ${config.baseUrl}`);

  // 테스트 계정 준비
  const user = getOrCreateTestUser();

  // 테스트용 상품 생성
  const timestamp = Date.now();
  const productRes = http.post(
    `${config.baseUrl}/products`,
    JSON.stringify({
      name: `K6 Baseline Product ${timestamp}`,
      description: 'Performance test product',
      price: 10000,
      stock: 100000, // 충분한 재고
      category: 'TEST',
    }),
    { headers: getAuthHeaders(user.accessToken) }
  );

  if (productRes.status !== 201) {
    console.error(`Product creation failed: ${productRes.status}`);
    console.error(`Response: ${productRes.body}`);
    throw new Error('Failed to create test product');
  }

  const productId = productRes.json('productId');

  console.log(`Setup complete - productId: ${productId}`);

  return {
    productId: productId,
    accessToken: user.accessToken,
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

  const success = check(res, {
    'status is 200 or 201': (r) => r.status === 200 || r.status === 201,
  });

  if (success) {
    orderSuccess.add(1);
  } else {
    orderFailed.add(1);
  }

  sleep(0.1);
}

export function teardown(data) {
  console.log(`Baseline Test Completed - productId: ${data.productId}`);
}
