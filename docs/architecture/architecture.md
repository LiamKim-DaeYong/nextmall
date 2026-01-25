# 아키텍처 정리

이 문서는 현재 구조와 역할을 최신 기준으로 정리한다.

---

## 1. 핵심 방향

- 서비스는 배포 단위로 분리되어 있다.
- 공통 로직은 common 모듈로 공유한다.
- Gateway는 외부 인증과 라우팅을 담당한다.
- BFF는 UI 집계와 요청 변환을 담당한다.
- Orchestrator는 다중 서비스 워크플로우(사가)를 담당한다.

---

## 2. 현재 구조(요약)

```text
nextmall/
├── common/                 # 공통 라이브러리
└── services/               # 배포 단위
    ├── api-gateway/        # WebFlux
    ├── bff-service/        # WebFlux
    ├── orchestrator-service/ # WebFlux
    ├── auth-service/       # MVC
    ├── user-service/       # MVC
    ├── product-service/    # MVC
    ├── order-service/      # MVC
    └── checkout-service/   # MVC
```

---

## 3. 역할 분리 기준

### Gateway
- Access Token 검증
- Passport Token 발급
- 라우팅

### BFF
- UI 집계
- 요청 변환
- 단순 조회/쓰기 요청 처리

### Orchestrator
- 다중 서비스 워크플로우(사가)
- 보상 트랜잭션 포함 흐름

### Domain Services
- 도메인별 책임
- 자체 데이터 관리

---

## 4. 요청 흐름(요약)

1) Client → Gateway: Access Token
2) Gateway → BFF/Orchestrator/Services: Passport Token
3) BFF/Orchestrator → Domain Services: Passport Token

---

## 5. 현재 선택의 배경(요약)

- WebFlux와 MVC 혼용 제약을 피하기 위해 배포 단위를 분리했다.
- 초기에는 BFF에 사가를 통합했으나, 역할 분리를 위해 Orchestrator를 도입했다.
- 내부 통신은 Passport Token을 기준으로 단순화했다.

---

## 6. 관련 문서

- [워크플로우 아키텍처](workflow-architecture.md)
- [Edge Authentication](../decisions/ADR-005-Edge-Authentication.md)
- [모듈 의존성 원칙](../decisions/ADR-004-모듈-의존성-원칙.md)
