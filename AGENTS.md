# NextMall

## 필수 컨텍스트

이 프로젝트를 이해하려면 반드시 아래 문서를 먼저 읽으세요:

- `docs/AI_CONTEXT.md` — 서비스 구조, 통신 맵, 코드 패턴, 현재 작업 상태
- `docs/TODO.md` — 우선순위별 TODO

## Codex 역할

- **실행 담당** (설계/플랜은 Claude에서 진행)
- 기존 코드 패턴을 반드시 따를 것
- 새 파일 작성 시 같은 서비스의 기존 파일 구조를 참고

## 코드 작성 규칙

- 테스트: Kotest `FunSpec` + `MockK` 사용 (`JUnit` 스타일 아님)
- 테스트 어노테이션: `common:test-support` 모듈의 `@IntegrationTest`, `@RedisIntegrationTest` 등 활용
- 서비스별 테스트 픽스처: 각 서비스의 `src/test/kotlin/.../fixture/`에 위치
- CQRS: 쓰기는 JPA Repository, 읽기는 jOOQ (`DSLContext`)
- 예외: 서비스별 `ErrorCode` enum 패턴 따를 것
- ID 생성: `IdGenerator` 사용 (Snowflake)

## 기술 스택 (빠른 참조)

- Spring Boot 4.0.1 / Kotlin / Gradle 멀티모듈
- 테스트: Kotest FunSpec + MockK + Testcontainers
- 인프라: PostgreSQL, Redis, Kafka
- 빌드: `./gradlew :services:<service-name>:test`

## 문서 참조

- 아키텍처: `docs/architecture/`
- 기술 결정: `docs/decisions/ADR-*.md`
