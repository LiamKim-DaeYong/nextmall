# IntelliJ 원클릭 기동 가이드 (초안)

목표: IntelliJ에서 버튼 한 번으로 전체 서비스 환경을 기동한다.

---

## 1. 구성 개요
- Docker Compose(인프라) + 서비스 모듈을 하나의 Compound Run으로 묶는다.
- 실행 순서를 정해 의존성 문제를 줄인다.

## 2. 사전 준비
- IntelliJ Ultimate 권장
- Docker Desktop 실행
- 프로젝트 열림 상태

## 3. 인프라 기동 설정
1) Run/Debug Configurations 열기
2) 새 구성 추가 → **Docker Compose**
3) `docker-compose.yml` 또는 인프라용 compose 파일 지정
4) 서비스: DB, Redis, Kafka 등 인프라 항목 선택
5) 이름 예: `infra-up`

## 4. 서비스 기동 설정
각 서비스에 대해 Spring Boot 실행 구성을 만든다.
- 예: `auth-service`, `api-gateway`, `user-service`, `product-service`, `order-service`, `checkout-service`, `bff-service`

## 5. Compound Run 구성
1) 새 구성 추가 → **Compound**
2) 순서대로 추가:
   - `infra-up`
   - `auth-service`
   - `api-gateway`
   - `user-service`
   - `product-service`
   - `order-service`
   - `checkout-service`
   - `bff-service`
3) 이름 예: `all-services`

## 6. 실행
- Run 버튼 한 번으로 전체 기동
- 첫 실행 후엔 인프라가 떠 있는지 확인

---

## 팁
- 각 서비스의 Profile(dev/local) 설정이 있다면 Run Configuration에 반영
- 메모리 부담이 크면 필요한 서비스만 Compound에 포함
