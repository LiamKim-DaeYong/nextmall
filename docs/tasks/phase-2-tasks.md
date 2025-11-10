# 🧩 Phase 2A - User 모듈 구축 (Week 3)

## 🎯 목표
- User 도메인(Entity, Repository, UseCase, Controller) 구축
- 회원가입 및 회원조회 API 구현
- 패스워드 암호화(BCrypt) 및 예외 처리 구조 확립
- Auth 모듈 개발(Phase 2B)에서 재사용 가능한 기반 설계

---

## 🏗️ 설계 개요

### 📦 모듈 구조
```yaml
modules/
└─ user/
├─ domain/
│  ├─ model/              # 순수 도메인 모델 (entity + value object)
│  ├─ repository/
│  └─ event/              # 도메인 이벤트 정의 (UserRegistered 등)
├─ application/
│  ├─ service/            # 도메인 로직 서비스
│  ├─ usecase/            # 회원가입, 프로필조회 등 구체적 시나리오
│  └─ validator/          # 검증 로직 (중복 회원 등)
└─ presentation/
├─ controller/
├─ dto/
└─ mapper/             # DTO ↔ Entity 변환

```

### 📘 주요 기능
- 회원가입 (POST `/api/users`)
- 회원 단건 조회 (GET `/api/users/{id}`)
- 이메일 중복 검증
- 비밀번호 암호화 (BCryptPasswordEncoder)
- Validation + ExceptionHandler 통합 구조 적용

---

## 🧾 Task 관리

| Task ID | Task 이름 | 설명 | 예상 시간 | 우선순위 | 상태 | 기술 |
|----------|------------|--------|-------------|----------|--------|--------|
| T7 | User 도메인 정의 | id, email, password, nickname, role 등 필드 정의 및 JPA 매핑 | 2h | ⭐ High | ✅ Done | Spring Data JPA |
| T8 | UserRepository 구현 | CRUD 및 이메일 중복 검증 쿼리 작성 | 1h | ⭐ High | ✅ Done | Spring Data JPA |
| T9 | RegisterUserUseCase 구현 | 회원가입 로직 + 패스워드 암호화(BCrypt) | 2h | ⭐ High | ✅ Done | Kotlin, BCrypt |
| T10 | FindUserUseCase 구현 | 회원 단건 조회 + 예외 처리 | 1h | High | ✅ Done | Kotlin |
| T11 | UserController 구현 | 회원가입/조회 API + DTO/Validation 적용 | 2h | ⭐ High | ✅ Done | Spring Web |
| T12 | ExceptionHandler 통합 | 중복 회원, Validation, EntityNotFound 등 공통 예외 처리 | 2h | Medium | ✅ Done | Spring Boot |
| T13 | 단위 테스트 추가 | UseCase, Repository, Controller 테스트 작성 | 3h | Medium | ✅ Done | Kotest, Mockk |

---

## 🧠 학습 포인트
- Spring Data JPA 및 엔티티 설계 전략
- DTO 변환 패턴 (Request/Response 분리)
- BCryptPasswordEncoder 적용 및 검증
- Controller 레이어 단위 테스트 작성법
- 예외 처리 표준화 (`@ControllerAdvice`)

---

## 🧩 산출물
- User 도메인 및 API 구현 완료
- 회원가입/조회 API Postman 테스트 컬렉션
- 테스트 코드 커버리지 리포트
- 이후 Auth 모듈(JWT) 연계 기반 완성

---

# 🔐 Phase 2B - Auth + Redis (Week 4)

## 🎯 목표
- JWT 기반 인증 시스템 구축
- Redis를 활용한 RefreshToken 저장 및 만료 관리
- Spring Security 필터체인 구성
- 권한(Role) 기반 접근 제어

---

## 🧾 Task 관리

| Task ID | Task 이름 | 설명 | 예상 시간 | 우선순위 | 상태 | 기술 |
|----------|------------|--------|-------------|----------|--------|--------|
| T14 | JWT 발급/검증 유틸 구현 | TokenProvider, Claims, SecretKey 관리 | 3h | ⭐ High | TODO | JWT, Kotlin |
| T15 | 로그인 API 구현 | UserService 인증 후 JWT 발급 | 3h | ⭐ High | TODO | Spring Security, JWT |
| T16 | 인증 필터 구성 | FilterChain 설정 및 JWT 검증 필터 추가 | 2h | ⭐ High | TODO | Spring Security |
| T17 | Redis 연동 | RefreshToken 저장, TTL 적용 | 2h | Medium | TODO | Redis |
| T18 | 로그아웃 및 블랙리스트 | 토큰 무효화 전략 적용 및 Redis 제거 처리 | 2h | Medium | TODO | Redis |
| T19 | 권한(Role) 기반 접근 제어 | BUYER / SELLER / ADMIN Role 분기 | 2h | Medium | TODO | Spring Security |

---

## 🧠 학습 포인트
- JWT 구조 및 Claims 설계
- RefreshToken 관리 전략 (Redis TTL/Blacklist)
- Spring Security FilterChain 커스터마이징
- 인증 예외 처리 및 테스트 작성
- Role 기반 접근 제어 전략

---

## 🧩 산출물
- JWT + Redis 기반 인증 시스템 완성
- 로그인/로그아웃 API
- Access/RefreshToken 발급 구조
- Redis 토큰 저장 구조 (key/value, TTL)
- 인증 필터 및 권한 분기 설정

---

## 📘 Velog 포스팅 아이디어
**제목:** 실무 인증 설계 따라잡기 - JWT + Redis 기반 인증 시스템  
**핵심 내용:**
- JWT 인증 구조 설계 철학
- RefreshToken Redis 저장 구조
- 실전 필터 구성 및 예외 처리 전략
- 회원(User) 도메인과의 연계 구조