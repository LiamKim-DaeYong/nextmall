import http from 'k6/http';
import { config } from './config.js';

/**
 * 로그인
 * @returns {string|null} accessToken
 */
export function login(email, password) {
  const res = http.post(
    `${config.baseUrl}/auth/login`,
    JSON.stringify({
      provider: 'LOCAL',
      principal: email,
      credential: password,
    }),
    { headers: { 'Content-Type': 'application/json' } }
  );

  if (res.status !== 200) {
    return null;
  }

  return res.json('accessToken');
}

/**
 * 회원가입
 * @returns {{ userId: number, accessToken: string }|null}
 */
export function signup(email, password, nickname) {
  const res = http.post(
    `${config.baseUrl}/sign-up/local`,
    JSON.stringify({ email, password, nickname }),
    { headers: { 'Content-Type': 'application/json' } }
  );

  if (res.status !== 201) {
    console.log(`Signup failed: ${res.status}`);
    return null;
  }

  return {
    userId: res.json('userId'),
    accessToken: res.json('accessToken'),
  };
}

/**
 * 테스트 계정 준비 (로그인 시도 → 실패 시 회원가입)
 * 환경변수: TEST_EMAIL, TEST_PASSWORD, TEST_NICKNAME
 */
export function getOrCreateTestUser() {
  const email = __ENV.TEST_EMAIL || `k6-test-${Date.now()}@test.com`;
  const password = __ENV.TEST_PASSWORD || 'Test1234!';
  const nickname = __ENV.TEST_NICKNAME || `K6 Test User`;

  // 먼저 로그인 시도
  let token = login(email, password);
  if (token) {
    console.log(`Using existing account`);
    return { email, accessToken: token };
  }

  // 로그인 실패 시 회원가입
  const user = signup(email, password, nickname);
  if (!user) {
    throw new Error(`Failed to create test user`);
  }

  console.log(`Created new test account`);
  return {
    email,
    userId: user.userId,
    accessToken: user.accessToken,
  };
}

export function getAuthHeaders(token) {
  return {
    'Content-Type': 'application/json',
    Authorization: `Bearer ${token}`,
  };
}
