# NextMall 프로젝트 컨텍스트

## 프로젝트 목적

- **학습 및 역량 향상**을 위한 이커머스 플랫폼
- 대규모 트래픽 처리, 분산 시스템 설계 등 실무에서 경험하기 어려운 영역 학습
- 실제 서비스 목적이 아닌 **기술 학습**이 주목적
- 설계 원칙: **실무 기준 베스트 프랙티스**

## 개발 환경

- 1인 개발
- 단일 Gradle 프로젝트 + 멀티 모듈 구조
- 한 번에 한 파일씩 작업하며 점진적으로 진행

## 기술 스택

| 영역 | 기술 | 비고 |
|------|------|------|
| Framework | Spring Boot 4.0.1 | 최신 버전 유지 |
| Language | Kotlin | |
| Command | JPA | 쓰기 작업 |
| Query | jOOQ | 읽기 작업 (CQRS) |
| Gateway | Spring Cloud Gateway | WebFlux 기반 |
| BFF | Spring MVC | 오케스트레이션 담당 |
| Database | PostgreSQL | |
| Cache | Redis | 세션, 캐시, 분산 락 |
| Message Queue | Kafka | 이벤트 드리븐 (예정) |
| Test | JUnit 5, Kotest, MockK | Testcontainers 활용 |

## 문서 참조 가이드

아키텍처나 정책처럼 변경 가능한 정보는 이 문서에 고정하지 않습니다.
항상 아래 문서/소스를 우선 참고합니다.

- **아키텍처/흐름**: `docs/architecture/` 문서
- **의사결정 기록**: `docs/decisions/ADR-*.md`
- **API 흐름 예시**: `http/` 및 `e2e-test/`
- **버전/의존성**: `gradle.properties`, `build.gradle.kts`, `settings.gradle.kts`
- **서비스 구성**: `services/` 하위 모듈 README 및 설정 파일

## 협업 규칙

1. **한 번에 한 파일씩**: 방향성 합의 후 클래스 파일 하나씩 작업
2. **Claude 역할**: 시니어 개발자로서 베스트 프랙티스 기반 가이드
3. **실무 수준 설계**: 학습 목적이지만 실제 운영 가능한 수준 지향
4. **코드 수정 전 확인**: 큰 변경은 방향성 먼저 논의

## 학습 목표 주제

- 대규모 트래픽 처리 설계
- 동시성 제어 (분산 락, 낙관적/비관적 락)
- 이벤트 드리븐 아키텍처 (Kafka)
- CQRS 패턴
- 마이크로서비스 간 통신 및 인증
- 장애 대응 (Circuit Breaker, Retry, Fallback)
- 캐시 전략 (Cache Aside, Write Through 등)
- 성능 테스트 (k6)
- 모니터링/로깅
