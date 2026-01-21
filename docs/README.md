# NextMall 문서

프로젝트의 아키텍처, 기술 결정, 운영 가이드를 정리한 문서입니다.

---

## 아키텍처

프로젝트 구조와 설계 원칙을 설명합니다.

| 문서 | 설명 |
|------|------|
| [아키텍처 발전 과정](architecture/evolution.md) | 모듈러 모놀리식 → MSA → Edge Auth 전환 히스토리 |
| [PBAC 인가 구조](architecture/authorization-pbac.md) | Policy-Based Access Control 설계 |

---

## 기술 결정 (ADR)

Architecture Decision Records - 주요 기술 선택과 근거를 기록합니다.

| ADR | 제목 | 요약 |
|-----|------|------|
| [ADR-001](decisions/ADR-001-JOOQ와-JPA-분리-전략.md) | CQRS (JPA + jOOQ) | Command는 JPA, Query는 jOOQ로 분리 |
| [ADR-002](decisions/ADR-002-모듈러-모놀리식에서-마이크로서비스로-전환.md) | MSA 전환 | WebFlux Gateway와 MVC 서비스 분리 |
| [ADR-003](decisions/ADR-003-Policy-모듈-도입.md) | Policy 모듈 | 정책을 코드에서 분리 |
| [ADR-004](decisions/ADR-004-PBAC-선택이유.md) | PBAC 선택 | RBAC 대신 Policy 기반 인가 |
| [ADR-005](decisions/ADR-005-BFF에서-사가-오케스트레이션-통합.md) | BFF + Saga | 초기 단계 오케스트레이션 전략 |
| [ADR-006](decisions/ADR-006-모듈-의존성-원칙.md) | 모듈 의존성 | 단방향 의존성 원칙 |
| [ADR-007](decisions/ADR-007-Edge-Authentication.md) | Edge Authentication | Netflix 패턴 적용, Gateway 토큰 처리 |

---

## 운영 가이드

개발 및 운영에 필요한 가이드입니다.

| 문서 | 설명 |
|------|------|
| [Liquibase 전략](database/liquibase-strategy.md) | DB 마이그레이션 운영 가이드 |

---

## 배경 및 경험

프로젝트의 배경이 된 실무 경험을 정리합니다.

| 문서 | 설명 |
|------|------|
| [물류 오케스트레이션](experience/logistics-orchestration.md) | Event Loop 기반 설계 실무 경험 |

---

## 읽는 순서 (권장)

1. **배경 이해**: [물류 오케스트레이션](experience/logistics-orchestration.md) - 왜 이 프로젝트를 시작했는지
2. **아키텍처 이해**: [아키텍처 발전 과정](architecture/evolution.md) - 전체 구조와 진화 방향
3. **핵심 결정**: [ADR-007 Edge Authentication](decisions/ADR-007-Edge-Authentication.md) - 현재 인증 구조
4. **세부 사항**: 관심 있는 ADR 선택해서 읽기
