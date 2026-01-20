# 실무 경험: 이벤트 루프 기반 오케스트레이션 설계

> 복잡한 도메인 로직의 순환 참조 문제를 해결하기 위해 설계한 아키텍처를 정리합니다.

---

## 프로젝트 개요

| 항목 | 내용 |
|------|------|
| **도메인** | 물류 시스템 |
| **역할** | 설계 ~ 운영 담당 |
| **환경 제약** | RDB만 사용, 비용 제한으로 외부 인프라 최소화 |

---

## 해결한 문제: 순환 비즈니스 로직

### 문제 상황

도메인 특성상 **비즈니스 로직이 순환 구조**였습니다:

```
A 처리 → B 이벤트 발생 → B 처리 → 다시 A 이벤트 발생
```

예를 들어:
- A 처리 중 특정 조건에서 B 이벤트 발생
- B 처리 중 다시 A 이벤트 발생
- 서로가 서로를 호출하는 구조

### 초기 시도: Facade 패턴

순환 참조를 끊기 위해 Facade 계층을 도입했으나:
- 복잡도 증가
- Facade가 비대해짐
- 근본적인 해결이 아님

### 최종 해결: Event Loop 기반 오케스트레이션

**단일 진입점**을 만들고, 이벤트 루프 방식으로 처리하도록 재설계했습니다.

---

## 아키텍처 상세

### 전체 흐름

```
Controller
    │
    ▼
EventCoordinator.dispatch(event)
    │
    ▼
┌─────────────────────────────────────────────────────────────────┐
│  EventLoopProcessor                                             │
│  @Transactional (단일 트랜잭션으로 정합성 보장)                    │
│                                                                  │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │  1. context.initialize(event)                              │ │
│  │  2. hooks.onStart(event, context)                          │ │
│  │                                                             │ │
│  │  3. while (queue.hasNext()) {          ← BFS 방식          │ │
│  │       event = queue.poll()                                  │ │
│  │                                                             │ │
│  │       hooks.beforeProcess(event, context)                   │ │
│  │       validator.validate(event)                             │ │
│  │       handler.handle(event, context)   ← 이벤트 핸들러 실행 │ │
│  │       hooks.afterProcess(event, context)                    │ │
│  │                                                             │ │
│  │       queue.addAll(context.popPendingEvents())              │ │
│  │     }                                                       │ │
│  │                                                             │ │
│  │  4. hooks.onComplete(event, context)                        │ │
│  └────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

### 핵심 컴포넌트

#### 1. EventCoordinator (진입점)

모든 비즈니스 로직의 **단일 진입점**입니다.

```
interface EventCoordinator {
    dispatch(event: DomainEvent)
    dispatch(event: DomainEvent): Result<T>  // 결과가 필요한 경우
}
```

- Controller에서 이벤트를 발행하면 Coordinator가 받아서 처리
- 결과가 필요한 경우 제네릭 Result 타입 반환

#### 2. EventLoopProcessor (이벤트 루프)

이벤트 루프를 실행하는 핵심 컴포넌트입니다.

**특징:**
- `@Transactional`: 전체 세션이 단일 트랜잭션
- **BFS 방식**: Queue에서 이벤트를 꺼내서 순차 처리
- **순환 감지**: 이벤트가 일정 개수 초과 시 무한 루프로 판단

```
@Transactional
function process(initialEvent, context) {
    context.initialize(initialEvent)
    queue = context.popPendingEvents()
    processedCount = 0
    maxEvents = 100

    hooks.onStart(initialEvent, context)

    while (queue.hasNext()) {
        if (++processedCount > maxEvents)
            throw "Too many events. Possible infinite loop"

        event = queue.poll()

        hooks.beforeProcess(event, context)
        validator.validate(event)
        handler.handle(event, context)
        hooks.afterProcess(event, context)

        // 핸들러에서 발행한 후속 이벤트 수집
        queue.addAll(context.popPendingEvents())
    }

    hooks.onComplete(initialEvent, context)
}
```

#### 3. Hook 시스템

횡단 관심사(검증, 로깅 등)를 비즈니스 로직에서 분리합니다.

**Session Hook**: 세션 단위 실행
- `onStart`: 세션 시작 전
- `onComplete`: 세션 종료 후
- `onError`: 세션 에러 시

**Event Hook**: 이벤트 단위 실행
- `beforeProcess`: 이벤트 처리 전
- `afterProcess`: 이벤트 처리 후
- `onEventError`: 이벤트 에러 시

**실행 순서:**
```
onStart
  └→ [beforeProcess → validate → handle → afterProcess] × N
onComplete
```

**의존성 관리:**

Hook 간 실행 순서가 중요할 때 우선순위로 선언합니다.

```
class ValidationHook implements EventHook {
    priority = 10  // 숫자가 낮을수록 먼저 실행
    runsBefore = [InventoryHook]  // 명시적 순서 지정
}
```

#### 4. ValidationPipeline

이벤트별 검증 로직을 Pipeline으로 실행합니다.

```
function validate(event) {
    validators = registry.getValidatorsFor(event)
                        .sortByPriority()

    for (validator in validators) {
        if (validator.supports(event)) {
            validator.validate(event)
            logger.record(event, "PASSED: " + validator.name)
        }
    }
}
```

**마커 인터페이스 활용:**

여러 이벤트에서 공통으로 필요한 검증은 마커 인터페이스로 처리합니다.

```
interface RequiresItemValidation {
    getValidationConditions(): List<Condition>
}

// 이벤트에 마커 상속
class CreateOrderEvent extends DomainEvent
                       implements RequiresItemValidation {

    getValidationConditions() {
        return [
            ItemExists(itemId),
            StockAvailable(itemId, quantity)
        ]
    }
}
```

onStart Hook에서 마커를 감지하여 자동으로 검증 실행.

#### 5. SessionLogger

ThreadLocal을 활용한 요청 추적 시스템입니다.

```
class SessionLogger {
    // ThreadLocal로 요청별 격리
    timeline = ThreadLocal<List<EventTrace>>()

    function onFinish(rootEvent, processedEvents, error) {
        summary = {
            "rootEvent": rootEvent.type,
            "status": error == null ? "SUCCESS" : "FAILED",
            "timeline": timeline.get()
        }
        log.info(toJson(summary))
        timeline.clear()
    }
}
```

**출력 예시:**
```json
{
  "rootEvent": "CreateOrderEvent",
  "status": "SUCCESS",
  "timeline": [
    {
      "event": "CreateOrderEvent",
      "handler": "OrderHandler",
      "validations": ["PASSED: StockValidator"],
      "hooks": ["LoggingHook", "MetricsHook"]
    },
    {
      "event": "DecreaseStockEvent",
      "handler": "StockHandler"
    }
  ]
}
```

---

## 테스트 DSL

시나리오 테스트의 의도를 명확하게 전달하기 위해 DSL을 설계했습니다.

```
scenario {
    setup {
        warehouse("A") {
            location("L1") {
                stock(item = "ITEM-001", qty = 10)
            }
        }
    }

    step {
        dispatch(CreateOrderEvent) {
            warehouse = "A"
            items = [("ITEM-001", 5)]
        }
    }

    expect {
        afterEvent(CreateOrderEvent) {
            order.status == CREATED
        }

        finally {
            stock("ITEM-001") == 5  // 10 - 5
            order.status == COMPLETED
        }
    }
}
```

**DSL 설계 의도:**
- `setup`: 테스트 데이터 생성
- `step`: 실행할 액션 정의
- `expect`: 검증 조건
  - `afterEvent(Event)`: 특정 이벤트 직후 검증
  - `finally`: 최종 상태 검증

---

## 설계 결정과 트레이드오프

### 왜 Event Loop인가?

| 대안 | 장점 | 단점 |
|------|-----|------|
| 직접 호출 | 간단 | 순환 참조 발생 |
| Facade 패턴 | 순환 해결 | Facade 비대화 |
| 메시지 큐 (Kafka) | 느슨한 결합 | 인프라 비용, 최종 일관성 |
| **Event Loop** | 순환 해결, 단일 트랜잭션 | 구현 복잡도 |

**선택 이유:**
- RDB만 사용 가능한 환경
- 정합성이 매우 중요한 도메인
- 단일 트랜잭션으로 롤백 보장

### 왜 ThreadLocal 로깅인가?

| 대안 | 장점 | 단점 |
|------|-----|------|
| 일반 로깅 | 간단 | 요청 추적 어려움 |
| MDC | 표준 방식 | 이벤트 간 흐름 추적 어려움 |
| 분산 추적 (Zipkin) | 표준, 시각화 | 인프라 필요 |
| **ThreadLocal** | 상세 추적, 인프라 불필요 | 비표준 |

**선택 이유:**
- 비용 제약으로 추가 인프라 불가
- 이벤트 간 흐름을 timeline으로 상세 추적 필요

---

## 성과

- **순환 참조 문제 해결**: 복잡한 도메인 로직을 안정적으로 처리
- **디버깅 용이성**: timeline 로그로 문제 원인 빠르게 파악
- **확장성**: 새로운 이벤트/핸들러 추가가 쉬움
- **테스트 용이성**: DSL로 시나리오 테스트 작성 간편

---

## NextMall에서의 발전

이 경험을 바탕으로 NextMall에서는 다음을 학습하고 있습니다:

| 실무 경험 | NextMall 학습 목표 |
|----------|-------------------|
| Event Loop (동기, 단일 트랜잭션) | Kafka (비동기, 최종 일관성) |
| 비관적 락 | 분산 락 + 낙관적 락 |
| ThreadLocal 로깅 | OpenTelemetry 분산 추적 |
| 단일 서비스 | MSA (Gateway, BFF, 도메인 서비스) |
