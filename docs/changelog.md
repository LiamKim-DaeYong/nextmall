# Changelog

## [v1.0.4] - 2025-11-06

### Gradle 멀티모듈 빌드 구조 및 bootJar 설정 수정

- Gradle 멀티모듈 환경에서 bootJar 관련 빌드 오류 해결
- 실행 모듈(api)과 라이브러리 모듈(user, common)의 빌드 방식을 명확히 분리하여 안정화
- 루트 build.gradle.kts의 Kotlin DSL 업데이트 및 컴파일 옵션(compilerOptions)을 최신 방식으로 정리
- api 모듈을 제외한 모든 모듈에서 bootJar 비활성화 및 jar 활성화
- JVM 타깃 버전을 21로 통일 (compilerOptions.jvmTarget = JvmTarget.JVM_21)
- ktlint 및 테스트 로깅(testLogging.events) 설정 정비
- clean, bootJar, jar 태스크 정상 동작 확인
- Gradle 빌드 구조 안정화 완료

---

## [v1.0.3] - 2025-11-04

### User 모듈 고도화 및 환경 구성 개선

- `user` 모듈에 회원가입 및 단일 사용자 조회 API 구현
- `RegisterUserUseCase` 리팩터링
  - 트랜잭션(`@Transactional`) 적용
  - `DuplicateEmailException` 도입으로 중복 예외 처리 개선
  - 검증 로직 및 비밀번호 암호화 구조 강화
- `UserMapper`의 NPE 위험 제거 및 매핑 안정성 향상 (`requireNotNull` 적용)
- JPA 설정 분리 (`application-dev.yml`, `application-prod.yml`)
  - `ddl-auto: update` / `validate` 환경별 구분
  - SQL 로그 출력 옵션 분리 (`show_sql`, `format_sql`)
- `.env` 기반 환경 변수 관리 구조 개선
  - 로컬 실행 시 IntelliJ EnvFile 플러그인으로 `.env` 자동 로드
  - Docker 환경에서는 `env_file`로 컨테이너 주입
- 데이터베이스 연결 설정을 `application.yml`에서 환경 변수 기반으로 변경
  - (`${DB_URL}`, `${DB_USERNAME}`, `${DB_PASSWORD}` 형태)
- common:security 모듈 추가
- common:data 모듈 추가
- common:util 모듈 추가

---

## [v1.0.2] - 2025-10-31

### ktlint 설정 및 테스트 환경 구축

- Gradle에 `ktlint` 린트 플러그인 설정 추가
- 공통 `subprojects`에 테스트 관련 의존성 구조 정의
- `api` 모듈에 `kotest`, `mockk`, `spring-boot-starter-test` 등 테스트 라이브러리 등록

### Docker 기반 개발환경 구성 완료

- docker-compose.yml을 이용한 PostgreSQL / Redis 환경 구성
- .env 파일로 환경 변수 관리
- 내부 네트워크 구성 및 컨테이너 간 통신 테스트 완료
- PostgreSQL 및 Redis 접속 확인 (CLI 테스트 포함)

---

## [v1.0.1] - 2025-10-29

### 초기 프로젝트 구조 및 API 구성

- `api` 모듈 생성 및 Spring Boot 실행 설정
- 멀티모듈 구조 설정 (`api`, `auth`, `user`)
- Health Check API 생성 및 테스트 완료
