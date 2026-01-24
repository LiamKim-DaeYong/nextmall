# NextMall 문서

프로젝트의 아키텍처, 기술 결정, 운영 가이드를 정리한 문서입니다.

---

## 아키텍처 (현재 기준)

프로젝트 구조와 설계 원칙을 설명합니다.

| 문서 | 설명 |
|------|------|
| [아키텍처 발전 과정](architecture/evolution.md) | 모듈러 모놀리식 → MSA → Edge Auth 전환 히스토리 |
| [PBAC 인가 구조](architecture/authorization-pbac.md) | Policy-Based Access Control 설계 |
| [공통화 및 컨벤션 기준](architecture/commonization-standards.md) | 서비스 간 공통화/컨벤션 기준 초안 |
| [이벤트 스키마 기준](architecture/event-schema.md) | Kafka 이벤트 스키마 최소 기준 |
| [서킷브레이커 최소 설계](architecture/circuit-breaker.md) | 최소 리질리언스 설계 기준 |
| [UCP 기반 쇼핑몰 설계](architecture/ucp-design.md) | UCP 프로토콜 기준의 쇼핑몰 설계 |
| [UCP Checkout API](architecture/ucp-checkout-api.md) | Checkout 최소 스키마/엔드포인트 |

---

## 기술 결정 (ADR, 현재 기준)

Architecture Decision Records - 주요 기술 선택과 근거를 기록합니다.

| ADR | 제목 | 요약 |
|-----|------|------|
| [ADR-001](decisions/ADR-001-JOOQ와-JPA-분리-전략.md) | CQRS (JPA + jOOQ) | Command는 JPA, Query는 jOOQ로 분리 |
| [ADR-002](decisions/ADR-002-모듈러-모놀리식에서-마이크로서비스로-전환.md) | MSA 전환 | WebFlux Gateway와 MVC 서비스 분리 |
| [ADR-003](decisions/ADR-003-Policy-모듈-도입.md) | Policy 모듈 | 정책을 코드에서 분리 |
| [ADR-004](decisions/ADR-004-PBAC-선택이유.md) | PBAC 선택 | RBAC 대신 Policy 기반 인가 |
| [ADR-005](decisions/ADR-005-BFF에서-사가-오케스트레이션-통합.md) | BFF + Saga | 초기 단계 오케스트레이션 전략 |
| [ADR-006](decisions/ADR-006-모듈-의존성-원칙.md) | 모듈 의존성 | 단방향 의존성 원칙 |
| [ADR-007](decisions/ADR-007-Edge-Authentication.md) | Edge Authentication | Gateway 토큰 처리 |
| [ADR-008](decisions/ADR-008-Orchestrator-분리.md) | Orchestrator 분리 | 분리 범위와 책임 정의 |

---

## 운영 가이드

개발 및 운영에 필요한 가이드입니다.

| 문서 | 설명 |
|------|------|
| [Liquibase 전략](database/liquibase-strategy.md) | DB 마이그레이션 운영 가이드 |
| [로컬 스모크 테스트 가이드](operations/local-smoke-test.md) | 게이트웨이 기반 최소 테스트 루틴 |
| [IntelliJ 원클릭 기동 가이드](operations/intellij-runbook.md) | IDE 기반 전체 서비스 기동 |

---

## 프로젝트 리뷰

프로젝트 상태 점검 및 개선 사항을 기록합니다.

| 문서 | 설명 |
|------|------|
| [아키텍처 리뷰 2026-01-24](reviews/ARCHITECTURE_REVIEW_2026-01-24.md) | 서비스 구조, 의존 관계, 가이드라인 |
| [프로젝트 리뷰 2026-01-23](reviews/PROJECT_REVIEW_2026-01-23.md) | 코드 품질, 보안, 일관성 점검 |

---

## 읽는 순서 (제안)

1. **아키텍처 이해**: [아키텍처 발전 과정](architecture/evolution.md) - 전체 구조와 진화 방향
2. **핵심 결정**: [ADR-007 Edge Authentication](decisions/ADR-007-Edge-Authentication.md) - 현재 인증 구조
3. **세부 사항**: 관심 있는 ADR 선택해서 읽기
