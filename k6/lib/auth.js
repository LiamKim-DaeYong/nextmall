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

  console.log(`Signup response: ${res.status} ${res.body}`);

  if (res.status !== 201) {
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

  console.log(`Attempting to get/create user: ${email}`);
  console.log(`BASE_URL: ${config.baseUrl}`);

  // 먼저 로그인 시도
  let token = login(email, password);
  if (token) {
    console.log(`Logged in as: ${email}`);
    return { email, accessToken: token };
  }

  // 로그인 실패 시 회원가입
  console.log(`Login failed, creating new user: ${email}`);

  const signupRes = http.post(
    `${config.baseUrl}/sign-up/local`,
    JSON.stringify({ email, password, nickname }),
    { headers: { 'Content-Type': 'application/json' } }
  );

  console.log(`Signup status: ${signupRes.status}`);
  console.log(`Signup body: ${signupRes.body}`);

  if (signupRes.status !== 201) {
    throw new Error(`Signup failed: ${signupRes.status} - ${signupRes.body}`);
  }

  return {
    email,
    userId: signupRes.json('userId'),
    accessToken: signupRes.json('accessToken'),
  };
}

export function getAuthHeaders(token) {
  return {
    'Content-Type': 'application/json',
    Authorization: `Bearer ${token}`,
  };
}
