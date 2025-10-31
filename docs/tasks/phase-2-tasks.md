# Phase 2 - 인증 시스템 + Redis 학습 (Week 3-4)

## 🎯 목표

* JWT 기반 인증 시스템 구현
* Redis를 활용한 Refresh Token 저장 및 세션 관리
* 실무에서 자주 사용하는 인증/인가 패턴 학습 및 적용
* 향후 Kafka 기반 이벤트 처리와 연계 가능하도록 설계

---

## 1. 설계 방향

### 🔐 인증 구조 개요

```
[Client] → [API Gateway / Web] → [Auth API]
                             ↘
                      [User API, Order API 등]
```

### 🔑 인증 흐름

* 회원가입, 로그인 API 구현
* 로그인 시 JWT 발급 (Access + Refresh)
* Refresh Token은 Redis에 저장 (TTL 적용)
* 로그아웃 시 Refresh Token 제거 (블랙리스트 optional)
* API 접근 시 JWT 인증 필터로 유효성 검사

### 🎭 권한 모델

* 기본 Role: BUYER / SELLER / ADMIN
* Spring Security 기반 권한 분기 (Method Security 포함 예정)

---

## 2. 학습 포인트

### ✅ JWT 핵심 요소

* AccessToken / RefreshToken 발급 전략
* JWT Claims 설계 (`userId`, `role`, `exp`, `iat` 등)
* JWT 검증 및 예외 처리 흐름

### ✅ Redis 활용 전략

* Redis에 RefreshToken 저장 (`key = userId`, `value = token`)
* TTL 적용을 통한 자동 만료
* 블랙리스트 저장 시 Set/List 등 자료구조 활용 고려
* Redisson 활용한 분산락 체험 (선택)

---

## 3. Task 관리

| Task ID | Task 이름                | 설명                                           | 예상 시간 | 우선순위   | 상태   | 기술                   |
| ------- | ---------------------- | -------------------------------------------- | ----- | ------ | ---- | -------------------- |
| T7      | User 도메인 정의 및 회원가입 API | User 엔티티, Repository, Service, Controller 구성 | 3h    | ⭐ High | TODO | Spring Data JPA      |
| T8      | JWT 로그인 API            | 패스워드 검증 및 Access/RefreshToken 발급             | 3h    | ⭐ High | TODO | Spring Security, JWT |
| T9      | JWT 발급/검증 모듈화          | TokenProvider, Claims 등 유틸 분리                | 3h    | ⭐ High | TODO | JWT, Kotlin          |
| T10     | 인증 필터 적용               | Security FilterChain 설정 및 검증                 | 2h    | High   | TODO | Spring Security      |
| T11     | Refresh Token Redis 저장 | Redis TTL, 토큰 저장 구조 설계                       | 2h    | Medium | TODO | Redis                |
| T12     | 로그아웃 및 블랙리스트 처리        | 토큰 무효화 전략 및 블랙리스트 적용                         | 2h    | Medium | TODO | Redis                |
| T13     | 권한 기반 Role 분리          | BUYER / SELLER / ADMIN 역할 분기 적용              | 2h    | Medium | TODO | Spring Security      |

---

## 4. 참고 예정 기술

* `spring-boot-starter-security`
* `jjwt` 또는 `nimbus-jose-jwt`
* `spring-data-redis`
* `Redisson` (선택)

---

## 5. 예상 산출물

* 회원가입 API 및 User 도메인 구성
* 로그인/로그아웃 API 및 테스트 코드
* JWT 발급/검증 유틸리티 클래스
* Redis 저장 구조 (key/value, TTL)
* 인증 필터 적용된 Spring Security 설정 파일
* Role 기반 권한 분기 샘플 코드

---

## 6. Velog 포스팅 계획 (선택)

**제목:** 실무 인증 설계 따라잡기 - JWT + Redis 기반 인증 시스템
**핵심 내용:**

* JWT 인증 구조 설계 철학
* Redis 세션/RefreshToken 저장 구조 비교
* 실전 필터 구성 및 에러 처리 전략
