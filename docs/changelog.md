## [v1.0.1] - 2025-10-29

**초기 프로젝트 구조 및 API 구성**

- `api` 모듈 생성 및 Spring Boot 실행 설정
- 멀티모듈 구조 설정 (`api`, `auth`, `user`)
- Health Check API 생성 및 테스트 완료

## [v1.0.2] - 2025-10-31

**ktlint 설정 및 테스트 환경 구축**

- Gradle에 `ktlint` 린트 플러그인 설정 추가
- 공통 `subprojects`에 테스트 관련 의존성 구조 정의
- `api` 모듈에 `kotest`, `mockk`, `spring-boot-starter-test` 등 테스트 라이브러리 등록
