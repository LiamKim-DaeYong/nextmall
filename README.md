# 🛒 NextMall

![CodeRabbit Pull Request Reviews](https://img.shields.io/coderabbit/prs/github/LiamKim-DaeYong/nextmall?utm_source=oss&utm_medium=github&utm_campaign=LiamKim-DaeYong%2Fnextmall&labelColor=171717&color=FF570A&link=https%3A%2F%2Fcoderabbit.ai&label=CodeRabbit+Reviews)

모듈러 모놀리스(Modular Monolith) 기반의 백엔드 아키텍처 학습 프로젝트입니다.
> ※ 단순한 레이어 분리가 아닌, 
> 서비스 경계와 통신을 명확히 분리한 구조를 지향합니다.

실제 MSA 환경을 혼자 개발 가능한 형태로 시뮬레이션하며,
엔터프라이즈 백엔드에서 요구되는 설계 판단, 경계 설정, 장애 대응을 중심으로 구성되어 있습니다.

이 프로젝트의 목적은 “완성된 서비스”가 아니라
설계 선택의 이유를 설명할 수 있는 백엔드 개발자로 성장하는 것입니다.

---

## 프로젝트 의도
- 실무에서 직접 다뤄보지 못한 기술과 아키텍처를 설계 관점에서 체득
- 단순 CRUD가 아닌, 경계·통신·실패·보안을 고려한 구조 연습
- GitHub Issue + PR + 리뷰 기반의 실무형 개발 프로세스 시뮬레이션
- AI(CodeRabbit, ChatGPT)와 협업하는 개발 방식 실험
> 실제 상용 서비스를 목표로 하지 않으며,
> 학습과 설계 검증을 위한 시뮬레이션 프로젝트입니다.

---

## 아키텍처 개요
**NextMall은 단일 JVM 기반 멀티모듈 구조를 사용하지만,
설계 사고방식은 서비스 분리를 전제로 합니다.**
- 각 모듈은 하나의 서비스처럼 설계됩니다
- 모듈 간 직접 참조를 최소화합니다
- 내부 통신은 WebClient를 사용해 서비스 간 호출을 흉내 냅니다
- API Gateway 역할의 엔트리 모듈이 모든 요청의 진입점이 됩니다

이를 통해 다음을 연습합니다:
- 서비스 경계 설정
- 내부 통신 실패/지연/예외 처리
- Gateway 책임 분리
- 인증/인가 흐름 통합

---

## 기술 스택

### Core
- Spring Boot 4.0.0
- Kotlin 2.2.x / Java 21
- Gradle (Kotlin DSL, Multi-module)

### Infrastructure / Data
- PostgreSQL
- Redis
- MongoDB
- Kafka
- Liquibase
- jOOQ

### Async / Communication
- Spring WebClient
- Spring WebFlux (선택적, 비동기 처리 전용)

### Test / Quality
- Kotest
- MockK
- SpringMockK
- SonarQube
- Ktlint

---

## 범위에서 제외한 것 (Non-goals)
- 실제 사용자 트래픽 처리
- 운영 비용 최적화 및 SLA 보장
- 즉시 상용 가능한 완성도
