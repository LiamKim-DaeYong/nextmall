# NextMall 장기 개선 로드맵 - 2026-01-24

## Scope

- 현재 상태를 기준으로 중장기 개선 방향을 정리
- 학습 목표와 운영 안정성을 함께 고려
- 세부 구현은 별도 문서로 위임

---

## 1. 현재 상태 요약

| 영역 | 상태 | 비고 |
|------|------|------|
| **테스트** | 인프라 우수, 커버리지 낮음 | Kotest + Testcontainers 구성됨 |
| **CI/CD** | 비활성화 | SonarCloud 파이프라인 주석 처리 |
| **관측성** | 진행 중 | 일부 서비스 OTEL 적용 |
| **API 문서화** | 없음 | OpenAPI/Swagger 미구성 |
| **이벤트 드리븐** | 인프라만 | Kafka 있으나 도메인 이벤트 미적용 |

---

## 2. 개선 우선순위(요약)

1) 테스트 기반 확충 (핵심 도메인 로직부터)
2) 이벤트 드리븐 도입 (Outbox 포함)
3) 관측성 기본값 정착 (Tracing/Logs/Metrics)
4) API 문서화 도입 (OpenAPI)
5) 성능/복원력 검증 (부하 테스트, Circuit Breaker)

---

## 3. 로드맵(간단)

### Phase 1: 기반 안정화
- 핵심 도메인 유닛 테스트 보강
- OTEL 적용 범위 확장
- OpenAPI 기본 설정

### Phase 2: 이벤트 시작
- Outbox 테이블 설계/적용
- 첫 도메인 이벤트 정의 및 소비자 1개 구현

### Phase 3: 관측성/복원력
- SLI/SLO 초안 수립
- 기본 대시보드 구성
- Circuit Breaker 적용 검토

---

## 4. 참고 문서

- [워크플로우 아키텍처](../architecture/workflow-architecture.md)
- [이벤트 스키마 기준](../architecture/event-schema.md)
- [Circuit Breaker 기준](../architecture/circuit-breaker.md)
- [OpenTelemetry 설정 템플릿](../observability/otel-config-template.md)
- [ADR-004 모듈 의존성 원칙](../decisions/ADR-004-모듈-의존성-원칙.md)
- [아키텍처 정리](../architecture/architecture.md)


