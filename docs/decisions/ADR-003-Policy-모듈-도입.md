# ADR-003: Policy 모듈 도입

> 정책-코드 결합도를 낮추기 위해 정책을 데이터로 관리하고, 런타임 변경 가능성을 열어둠

## 상태
채택됨 (Accepted)

## 배경

### 정책-코드 결합도 문제

정책이 코드에 강하게 결합되면 변경 비용이 커지고, 변경 주기 차이를 관리하기 어렵다.

## 대안 비교

### 1. 현상 유지 (전략 패턴)
```kotlin
class TechnicianInstallStrategy : DeliveryStrategy {
    override fun canApply(context: DeliveryContext): Boolean {
        return context.warehouse.type == "TECHNICIAN"
    }
}
```
- ✅ 코드 구조 개선
- ❌ 정책이 여전히 코드에 강하게 결합
- ❌ 정책 변경 시 배포 필요

### 2. 설정 파일 기반 (YAML/JSON)
```yaml
policies:
  - name: "설치기사 즉시 완료"
    conditions:
      warehouse.type: "TECHNICIAN"
      delivery.type: "INSTALLED"
    action: "COMPLETE"
```
- ✅ 정책을 데이터로 관리
- ❌ 표현력 제한 (복잡한 조건 표현 어려움)
- ❌ 타입 안전성 한계

### 3. Policy 엔진 (선택)
```kotlin
policy("delivery", "complete") {
    name = "설치기사 즉시 완료"
    
    allow {
        condition("warehouse.type", Operator.EQUALS, "TECHNICIAN")
        condition("delivery.type", Operator.EQUALS, "INSTALLED")
    }
}
```
- ✅ 정책을 데이터로 관리
- ✅ DSL로 표현력 확보
- ✅ 타입 안전성 확보
- ✅ 런타임 정책 변경 가능
- ⚠️ 초기 구현 복잡도 증가

## 결정

Policy 모듈을 도입하여 정책을 데이터로 관리한다.

### 핵심 선택 이유

1. 정책과 코드의 변경 주기 분리
2. 정책을 데이터로 다룰 수 있는 구조 확보
3. 인가 정책(PBAC)부터 단계적으로 적용

## 구현 방식

### Policy DSL
```kotlin
policy("resource", "action") {
    name = "정책 이름"
    description = "정책 설명"
    
    allow("규칙 이름") {
        condition("subject.role", Operator.EQUALS, "ADMIN")
        condition("resource.status", Operator.IN, listOf("PENDING", "APPROVED"))
    }
    
    deny("거부 규칙") {
        condition("resource.deleted", Operator.EQUALS, true)
    }
}
```

### Policy 평가
```kotlin
interface PolicyEngine {
    fun evaluate(
        resource: String,
        action: String,
        subject: Map<String, Any>,
        resourceData: Map<String, Any>
    ): PolicyDecision
}

data class PolicyDecision(
    val allowed: Boolean,
    val reason: String?
)
```

### Policy 저장소
```kotlin
interface PolicyRepository {
    fun findByResourceAndAction(resource: String, action: String): Policy?
    fun save(policy: Policy)
    fun delete(resource: String, action: String)
}
```

## 적용 범위

### 1단계: 인가 정책 (PBAC)
```kotlin
policy("user", "read") {
    allow("관리자 전체 조회") {
        condition("subject.roles", Operator.CONTAINS, "ADMIN")
    }
    
    allow("본인 정보 조회") {
        conditionRef("subject.userId", Operator.EQUALS, "resource.id")
    }
}
```

### 2단계: 비즈니스 로직 정책 (향후)
```kotlin
policy("delivery", "complete") {
    allow("설치기사 즉시 완료") {
        condition("warehouse.type", Operator.EQUALS, "TECHNICIAN")
        condition("delivery.type", Operator.EQUALS, "INSTALLED")
    }
}

policy("order", "cancel") {
    allow("주문 취소 가능") {
        condition("order.status", Operator.IN, listOf("PENDING", "CONFIRMED"))
        condition("order.paymentStatus", Operator.NOT_EQUALS, "COMPLETED")
    }
}
```

## 트레이드오프

### 얻는 것
- 정책 변경의 독립성
- 정책 관리 범위 확장 가능성

### 포기하는 것
- 초기 구현 복잡도 증가
- 정책 테스트 도구 필요

## 향후 고려사항

- 정책 버전 관리
- 정책 캐싱
