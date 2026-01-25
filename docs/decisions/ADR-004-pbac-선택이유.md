# ADR-004: PBAC 기반 인가 방식 선택

> Policy 모듈을 활용한 속성 기반 동적 인가. RBAC의 한계를 극복하고 Policy as Data 패턴 적용

## 상태
채택됨 (Accepted)

## 배경

### Policy 모듈 도입 배경

[ADR-003](ADR-003-Policy-모듈-도입.md)에서 정책-코드 결합도를 낮추기 위해 Policy 모듈을 도입했다.

#### 핵심 인사이트: 인가도 정책의 일종
- 물류: "설치기사 창고 + INSTALLED 출고 → 즉시 완료"
- 인가: "관리자 역할 + 본인 리소스 → 접근 허용"
- 공통점: 조건 평가 후 액션 결정

Policy 모듈이 이미 존재하므로, 인가 시스템도 이를 활용하는 것이 자연스럽다.

### RBAC의 한계

RBAC를 사용할 때 다음과 같은 한계를 고려했다:

```kotlin
// 역할만으로는 표현할 수 없는 복잡한 권한 조건
if (user.role == "MANAGER" 
    && resource.department == user.department 
    && resource.status == "PENDING") {
    // 승인 가능
}
```

#### 문제점:
- 리소스 속성 기반 제어 불가 (예: "본인 주문만 조회")
- 역할이 코드에 하드코딩
- 도메인 확장 시 역할 폭발 문제

## 인가 방식 비교

| 구분 | RBAC | ABAC | PBAC |
|------|------|------|------|
| **제어 단위** | 역할(Role) | 속성(Attribute) | 정책(Policy) |
| **표현력** | 낮음 | 매우 높음 | 높음 |
| **리소스 기반 제어** | 불가 | 가능 | 가능 |
| **구현 복잡도** | 낮음 | 높음 | 중간 |
| **정책-코드 분리** | 불가 | 부분 가능 | 가능 |
| **정책 변경 시** | 코드 수정 필요 | 코드 수정 필요 | 정책만 수정 |
| **Policy 모듈 활용** | 불가 | 부분 가능 | 완전 활용 |

### RBAC를 선택하지 않은 이유
- 역할만으로는 표현하기 어려운 케이스 존재
- 리소스 스코프 제어 불가
- 역할이 코드와 강하게 결합

### ABAC를 선택하지 않은 이유
- ABAC는 **개념**(속성 기반 접근 제어)
- PBAC는 **구현 방식**(정책을 데이터로 관리)
- 본 프로젝트는 ABAC를 PBAC 방식으로 구현한 것
- Policy 모듈과 일관된 설계를 위해 PBAC 용어 선택

## 결정

**PBAC (Policy-Based Access Control)** 방식을 채택한다.

### 핵심 선택 이유

#### 1. Policy 모듈과 일관된 설계
- Policy 모듈이 이미 존재 (ADR-003)
- 인가도 정책의 일종이므로 동일한 엔진 활용
- 비즈니스 로직 정책과 인가 정책을 동일한 방식으로 관리

#### 2. 속성 기반 동적 인가
- 리소스 속성에 따른 동적 권한 평가
- "본인 주문만 조회", "같은 부서만 승인" 등 복잡한 조건 표현 가능
- 런타임에 정책 변경 가능

#### 3. 정책과 코드의 분리
- 정책 변경이 코드 변경을 유발하지 않음
- 정책은 데이터로 관리
- 배포 없이 권한 정책 수정 가능

#### 4. 확장 가능성
- 초기: 인가 정책
- 향후: 비즈니스 로직 정책도 동일한 패턴으로 확장

## 구현 방식

### 정책 정의 (DSL)
```kotlin
policy("user", "read") {
    name = "사용자 조회 정책"
    
    allow("관리자 전체 조회") {
        condition("subject.roles", Operator.CONTAINS, "ADMIN")
    }
    
    allow("본인 정보 조회") {
        conditionRef("subject.userId", Operator.EQUALS, "resource.id")
    }
}

policy("order", "cancel") {
    name = "주문 취소 정책"
    
    allow("관리자 전체 취소") {
        condition("subject.roles", Operator.CONTAINS, "ADMIN")
    }
    
    allow("본인 주문 취소") {
        conditionRef("subject.userId", Operator.EQUALS, "resource.userId")
        condition("resource.status", Operator.IN, listOf("PENDING", "CONFIRMED"))
    }
}
```

### 정책 평가
```kotlin
@RequiresPolicy(resource = "user", action = "read")
fun getUser(@PathVariable userId: Long): UserResponse {
    // 정책 평가는 AOP에서 자동 처리
    // 허용되지 않으면 AccessDeniedException 발생
}
```

### 정책 동기화
```
auth-service (정책 변경)
  → Kafka (PolicyChangedEvent)
  → 각 서비스 (캐시 갱신)
```

## 트레이드오프

### 얻는 것
- Policy 모듈과 일관된 설계
- 속성 기반 동적 인가
- 정책 변경 시 배포 불필요
- 비즈니스 로직 정책으로 확장 가능

### 포기하는 것
- RBAC 대비 초기 구현 복잡도 증가
- 정책 DSL 학습 비용
- 정책 테스트 도구 필요

### 리스크 완화
- 정책 DSL 문서 정비
- 정책 테스트 도구 제공
- 단계적 적용 (간단한 정책부터)

## 향후 고려사항

### 정책 성능 최적화
- 정책 캐싱
- 정책 평가 최적화

### 다양한 도메인 적용
- 인가 정책 (현재)
- 비즈니스 로직 정책 (향후)
