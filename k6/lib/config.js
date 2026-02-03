export const config = {
  baseUrl: __ENV.BASE_URL || 'http://localhost:8080/api/v1',

  // 테스트 시나리오별 설정
  scenarios: {
    smoke: { vus: 1, duration: '10s' },
    load: { vus: 50, duration: '1m' },
    stress: { vus: 100, duration: '2m' },
    spike: {
      stages: [
        { duration: '10s', target: 10 },
        { duration: '1m', target: 100 },
        { duration: '10s', target: 10 },
      ],
    },
  },

  // 성공 기준
  thresholds: {
    http_req_duration: ['p(95)<500'], // 95% 요청 500ms 이내
    http_req_failed: ['rate<0.01'], // 실패율 1% 미만
  },
};
