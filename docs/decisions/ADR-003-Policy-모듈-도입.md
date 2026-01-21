# ADR-003: Policy 모듈 도입

> 실무에서 겪은 정책-코드 강결합 문제 해결. 정책을 데이터로 관리하여 코드 변경 없이 런타임 정책 변경 가능

## 상태
채택됨 (Accepted)

## 배경

### 실무에서 겪은 정책-코드 강결합 문제

현재 물류 도메인 실무에서 **정책 관리**의 어려움을 겪고 있다:

**정수기 렌탈 사업의 복잡한 검증 정책**
- 설치기사(창고로 관리) 할당 가능 여부 검증 → 분기 로직 과다
- 차용재고 정책: 부품/소모품은 사전 신청 후 차량 보관 가능, 제품/필터는 당일 배정-설치-반납 필수
- 시리얼번호 기반 재고 추적 및 상태 관리
- 입출고 유형별 상이한 처리 로직 (설치 출고, 회수 입고, 교체, 이동 등)

**정책 구현의 변천사**
```kotlin
// 초기: if문 도배
if (warehouse.type == "TECHNICIAN" && delivery.type == "INSTALLED") {
    delivery.status = "COMPLETED"
} else if (warehouse.type == "NORMAL" && delivery.type == "INSTALLED") {
    delivery.status = "SCHEDULED"
} else if (/* 수십 개의 조건 */) {
    // ...
}
```

```kotlin
// 개선: 전략 패턴 + 파이프라인
class InstallationCompletionStrategy : DeliveryStrategy {
    override fun canApply(context: DeliveryContext): Boolean {
        return context.warehouse.type == "TECHNICIAN" 
            && context.delivery.type == "INSTALLED"
    }
    
    override fun execute(context: DeliveryContext) {
        context.delivery.status = "COMPLETED"
    }
}
```

**여전히 남은 문제**
- 정책이 코드에 강결합: `warehouse.type == "TECHNICIAN"` 같은 조건이 코드에 하드코딩
- 정책 변경 시 코드 수정 불가피 → 배포 필요
- SRP 위반: 코드의 변경 사유가 "정책1 변경", "정책2 변경", "비즈니스 로직 변경" 등 너무 많음

### 핵심 인사이트

**정책은 언제든 변할 수 있는데, 코드에 강결합되면 유연성 상실**

- 정책 변경 → 코드 변경 → 배포 → 버그 발생의 악순환
- 변경의 빈도가 다름: 정책은 자주 변경, 코드는 안정적이어야 함
- 변경의 사유가 다르므로 분리되어야 함

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
- ❌ 정책이 여전히 코드에 강결합
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
- ❌ 타입 안전성 부족

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

**Policy 모듈을 도입**하여 정책을 데이터로 관리한다.

### 핵심 선택 이유

**1. 정책과 코드의 분리**
- 정책 변경이 코드 변경을 유발하지 않음
- 정책은 데이터로 관리되어 런타임에 변경 가능
- SRP 준수: 코드의 변경 사유는 "비즈니스 로직 변경"으로 단일화

**2. 변경의 빈도 차이 인정**
- 정책은 언제든 변경될 수 있어야 함 (높은 유연성)
- 코드는 정책만큼 자주 변경되어서는 안 됨 (안정성)
- 변경의 사유가 다르므로 분리되어야 함

**3. 확장 가능성**
- 초기: 인가 정책 (PBAC)
- 향후: 비즈니스 로직 정책 (상태 전이, 액션 결정 등)
- 동일한 Policy 엔진으로 다양한 도메인 정책 관리

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
- 정책 변경 시 배포 불필요
- 코드 변경 사유 단순화 (SRP)
- 도메인 확장 시 유연한 대응
- 다양한 도메인에 동일한 패턴 적용 가능

### 포기하는 것
- 초기 구현 복잡도 증가
- Policy 엔진 및 DSL 학습 비용
- 정책 테스트 도구 필요

### 리스크 완화
- 명확한 Policy DSL 문서 작성
- 정책 테스트 도구 제공
- 단계적 적용 (간단한 정책부터)

## 향후 고려사항

**정책 버전 관리**
- 정책 변경 시 롤백 가능하도록
- A/B 테스트 지원

**정책 성능 최적화**
- 정책 캐싱
- 정책 평가 최적화 (인덱싱, 조기 종료 등)

**다양한 도메인 적용**
- 인가 정책 (PBAC) ✅
- 비즈니스 로직 정책 (상태 전이, 액션 결정)
- 검증 정책 (입력 검증, 비즈니스 규칙 검증)
