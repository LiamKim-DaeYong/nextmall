# PRD (Product Requirements Document) - NextMall(e-commerce)

## 📄 Document Information
- Version: 1.0.0
- Created: 2025-10-26
- Author: Backend Developer (10년차)
- Status: In Progress

---

## 1. Project Overview

### 🎯 목적
- 이직을 위한 기술 스택 증명형 포트폴리오 구축
- 실무에서 경험하지 못한 핵심 기술을 실제 프로젝트에 도입
- 대규모 트래픽 및 분산 환경 대응 능력 실습
- 기술 블로그 시리즈 포스팅과 병행하여 학습 결과 기록
- 프로젝트를 통한 자기 확신 및 기술력 회복

### ✅ 성공 기준 (Success Criteria)
- [ ] Kafka, Redis, MongoDB, Docker, WebFlux, AWS 실무 적용
- [ ] 트랜잭션, 캐싱, 메시징, 비동기 처리 등 실무 핵심 패턴 학습
- [ ] 대규모 트래픽 대응 (성능 테스트 포함)
- [ ] Event-Driven Architecture 구성 및 처리 흐름 구성
- [ ] AWS 환경에서 배포/운영 및 모니터링 경험 확보
- [ ] Velog에 각 Phase별 실전 포스팅 발행

---

## 2. Technical Stack

### ⚙️ 핵심 기술 스택
```yaml
Backend: Spring Boot 3.2+, Java 21
Build: Gradle 8+ (Kotlin DSL, Multi-module)
Database:
  - PostgreSQL 15+ (User, Product, Order)
  - MongoDB 7+ (Review, Activity Log)
  - Redis 7+ (Session, Cache, Distributed Lock)
Message Queue: Apache Kafka 3+
Search: Elasticsearch 8+ (선택사항)
Async: Spring WebFlux (Notification 전용)
Container: Docker + Docker Compose
Cloud: AWS (ECS, RDS, MSK, ElastiCache)
```

### 🧪 비기능 요구사항
- 성능: 평균 응답 시간 < 200ms, TPS 1,000 이상
- 가용성: 99.9% SLA 수준
- 확장성: 수평 확장 기반 설계
- 보안: OWASP Top 10 대응

## 3. System Architecture
### 🧱 아키텍처 구성
```scss
Client → API Gateway
             ↓
        ┌────────────┬─────────────┬────────────┐
     UserService  ProductService  OrderService
           │               │              │
           └────→ Kafka Event Bus ←───────┘
                     ↓
          NotificationService (WebFlux)
                     ↓
                Infra (Redis, DB)

```

### 📦 모듈 구조
```bash
/api                         ← API 진입점 (Spring MVC)
/modules
  ├─ user                   ← 인증, 사용자 관리
  ├─ product                ← 상품/카테고리
  ├─ order                  ← 주문, 재고, 결제
  ├─ review                 ← MongoDB 기반 리뷰
  ├─ notification           ← 비동기 알림 (WebFlux)
  ├─ common                 ← 공통 DTO, Error, Config
/infrastructure              ← Kafka, Redis, DB 설정 모듈
/docker                     ← docker-compose, init.sql 등
```

## 4. 개발 및 학습 워크플로우
### Phase 1 (Week 1-2): 프로젝트 기반 구축 + Docker 학습
#### Step 1: 프로젝트 세팅 + Docker 환경 구성
- [ ] Gradle 멀티모듈 프로젝트 생성
- [ ] Docker Compose로 PostgreSQL, Redis 구성
- [ ] 기본 헬스체크 API 구현

**문제 인식**: 환경 구성의 불일치, DB 초기화 반복

Docker 학습 전략:
```yaml
실습 위주 → 문제 발생 → 아키텍처 학습
  1. 컨테이너로 PostgreSQL, Redis 실행
  2. Docker 네트워크 이해
  3. 개발용 + 운영용 분리 전략 고민
```
**Velog 포스팅**: Docker 입문 - 개발환경 통일하기

### Phase 2 (Week 3-4): 인증 시스템 + Redis 학습
#### Step 2: JWT 인증 구현
- [ ] 회원가입/로그인 API
- [ ] JWT 토큰 생성 및 필터 적용
- [ ] 권한 기반 Role 분리 (BUYER, SELLER, ADMIN)

**문제 인식**: 로그아웃/토큰 무효화의 어려움

#### Step 3: Redis + 세션 관리
- [ ] Refresh Token Redis 저장
- [ ] 로그아웃 시 블랙리스트 처리
- [ ] 사용자 세션 캐싱
- [ ] 동시 로그인 제한 기능

Redis 학습 전략:
```yaml
  1. JWT 한계 분석 → 세션 필요성 인식
  2. Redis 데이터 타입 학습 + TTL 적용
  3. Redisson 기반 분산락 학습 병행
```
**Velog 포스팅**: Redis 실무 활용 - 세션 관리와 캐싱 전략

### Phase 3 (Week 5-6): 상품 관리 + MongoDB 리뷰
#### Step 4: 상품 CRUD + RDB 성능 튜닝
- [ ] 상품/카테고리 CRUD
- [ ] 쿼리 인덱싱 및 실행계획 분석
- [ ] 페이징 + 정렬 최적화

#### Step 5: 리뷰 시스템 MongoDB 전환
- [ ] 리뷰 CRUD (사진, 태그 포함)
- [ ] 평점 집계 API
- [ ] 상품별 리뷰 검색/필터링

MongoDB 학습 전략:
```yaml
    1. NoSQL 도입 판단 기준 확립
    2. Document 모델 설계 전략 학습
    3. MongoRepository vs Template 사용 비교
```

**Velog 포스팅**: NoSQL은 언제 쓰는가 - MongoDB 리뷰 설계기

### Phase 4 (Week 7-8): 주문 시스템 + Kafka 이벤트 기반
#### Step 6: 주문 → 결제 동기 흐름 구현
- [ ] 장바구니 → 주문 → 결제 연동
- [ ] 재고 분산락, 실패 시 롤백

#### Step 7: Kafka 이벤트 기반 전환
- [ ] Kafka 기본 발행/소비 구성
- [ ] 주문-결제-배송 사가 패턴 구현
- [ ] 이벤트 리트라이 및 실패 처리 전략 도입

Kafka 학습 전략:
```yaml
    1. 주문 성공 후 이벤트 발행 → 알림 전송
    2. Kafka + Saga → 트랜잭션 보강 실습
    3. Topic 설계 및 메시지 역직렬화 전략 학습
```

**Velog 포스팅**: Kafka로 만드는 이벤트 기반 주문 시스템

### Phase 5 (Week 9-10): WebFlux 알림 + AWS 배포
#### Step 8: WebFlux 기반 알림 서비스
- [ ] 이메일/푸시 발송 비동기 처리
- [ ] 외부 API (SendGrid 등) 연동
- [ ] 처리량 비교 분석

WebFlux 학습 전략:

```yaml
    1. Reactive 개념 학습
    2. Controller 단 WebFlux 전환
    3. RestTemplate ↔ WebClient 비교
```

#### Step 9: AWS 배포 및 운영 구성
- [ ] Docker 이미지 → ECS 배포
- [ ] RDS + Redis + MSK 구성
- [ ] CloudWatch + Prometheus 모니터링
- [ ] 비용 모니터링 및 최적화

**Velog 포스팅**: AWS ECS 기반 백엔드 운영 환경 구축기

## 5. Milestone Tracking
### ✅ 전체 진행 상황
- Current Phase: Phase 1
- Current Week: Week 1
- Progress: 0%

### 🔎 Phase 완료 기준
#### ✅ Phase 1
- [ ] Docker 개발환경 구축 완료
- [ ] Gradle 멀티모듈 구성 완료
- [ ] PostgreSQL 연결 성공
- [ ] Health Check API 정상 작동
- [ ] Velog 1편 발행

#### ⏳ Phase 2
- [ ] JWT 인증/인가 구현
- [ ] Redis 세션 관리 적용
- [ ] 사용자 캐싱 + TTL 관리
- [ ] Velog 1편 발행

