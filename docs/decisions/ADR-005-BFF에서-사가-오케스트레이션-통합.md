# ADR-005: BFF에서 사가 오케스트레이션 통합

> 초기 단계에서 사가 플로우와 BFF API는 함께 변경됨. 변경 포인트 최소화를 위해 BFF에 통합, 안정화 후 분리 검토

## 상태
채택됨 (Accepted)

## 배경

마이크로서비스 아키텍처에서 분산 트랜잭션을 처리하기 위해 사가 패턴을 도입했다. 일반적으로 사가 오케스트레이터는 별도 서비스로 분리하는 것이 권장되지만, 현재 프로젝트에서는 BFF(Backend For Frontend)에 통합하여 구현했다.

### 실무에서 겪은 불확실성

신규 법인 물류 시스템을 구축하여 현재 운영 중이다. 신규 사업 특성상 다음과 같은 환경에서 개발했으며, 운영 단계에서도 상황은 크게 달라지지 않았다:

- **기획 문서 부재**: 상세한 기획서나 요구사항 명세 없이 시작
- **QA 프로세스 부재**: 테스트 케이스나 검증 기준 미수립
- **정책 미정의**: 사업팀도 물류 경험 부재로 비즈니스 정책 정의 불가
- **도메인 전문가 부재**: 개발팀 전원 물류 경험 없음, 국내 케이스와 달라 참고 불가
- **개발 방식**: 개발팀 주도로 도메인 학습 및 요구사항 도출

이는 신규 사업 초기 단계의 전형적인 상황이며, 운영 중에도 지속적인 실험과 개선이 필요한 환경이다.

### 핵심 인사이트

**불확실한 초기 단계에서는 변경 포인트를 최소화하고 유사한 것들을 모아야 한다**

- 정책이 없으면 요구사항이 빈번히 변경됨
- 별도 서비스로 분리하면 변경 시 여러 곳을 수정해야 함
- 초기에는 응집도를 높여 변경에 빠르게 대응하는 것이 중요
- 도메인이 안정화되면 그때 분리 고려

### 초기 단계의 불확실성
- **도메인 경계 불명확**: 어떤 책임을 어디에 둘지 아직 확정되지 않음
- **API 조합 미확정**: 클라이언트 요구사항에 따라 API 조합이 자주 변경될 가능성
- **서비스 호출 패턴 불안정**: 각 서비스를 어떤 순서로, 어떻게 호출할지 실험 단계

### BFF와 사가 오케스트레이터의 공통 관심사

**BFF의 고민**
- 어떤 API를 조합해야 하나?
- 각 서비스를 어떤 순서로 호출해야 하나?
- 어떤 데이터를 어떻게 조합해서 클라이언트에 전달할까?

**사가 오케스트레이터의 고민**
- 어떤 서비스를 호출해야 하나?
- 어떤 순서로 호출해야 하나?
- 실패 시 어떻게 보상 트랜잭션을 실행할까?

**→ 둘 다 "서비스 간 조율"이라는 동일한 문제를 다룸**

## 대안 비교

| 구분 | BFF 통합 | 별도 오케스트레이터 서비스 |
|------|----------|---------------------------|
| **초기 구현 복잡도** | 낮음 | 높음 |
| **변경 용이성** | 높음 (한 곳만 수정) | 낮음 (두 곳 수정) |
| **책임 분리** | 낮음 | 높음 |
| **인프라 복잡도** | 낮음 | 높음 (서비스 추가) |
| **확장성** | 제한적 | 높음 |
| **학습 곡선** | 낮음 | 높음 |

### 별도 오케스트레이터 서비스를 선택하지 않은 이유
- 초기 단계에서 도메인 경계가 불명확하여 책임 분리의 이점이 크지 않음
- 사가 플로우 변경 시 BFF API도 함께 변경되는 경우가 대부분
- 별도 서비스 추가로 인한 인프라 복잡도 증가
- 토이 프로젝트 특성상 실제 트래픽이 없어 확장성 이점이 미미함

## 결정

**BFF에서 사가 오케스트레이션을 통합**하여 구현한다.

### 핵심 선택 이유

**1. 변경의 동시성**
- 사가 플로우 변경 = BFF API 변경과 거의 동시 발생
- 함께 변경되는 것들은 함께 있는 게 합리적 (Common Closure Principle)
- 두 곳을 동시에 수정하는 오버헤드 제거

**2. 초기 단계의 유연성**
- 도메인이 안정화되지 않은 상태에서 빠른 실험 가능
- 서비스 호출 패턴을 쉽게 변경하며 최적의 구조 탐색
- 조기 최적화(premature optimization) 방지

**3. 학습 목적 부합**
- 사가 패턴 자체를 이해하는 것이 우선 목표
- 서비스 분리는 부차적인 문제
- 복잡도를 낮춰 핵심 개념에 집중

**4. 인프라 단순화**
- 별도 서비스 없이 배포 및 운영 복잡도 감소
- 토이 프로젝트 특성상 과도한 분산 불필요

## 구현 방식

### 사가 정의 (Facade)
```kotlin
@Component
class SignUpFacadeImpl(
    private val userServiceClient: UserServiceClient,
    private val authServiceClient: AuthServiceClient,
) : SignUpFacade {
    override suspend fun signUp(command: SignUpCommand): SignUpResult {
        // 1. User 생성 (PENDING)
        val userId = userServiceClient.createUser(
            nickname = command.nickname,
            email = null,
        )

        try {
            // 2. Auth 계정 생성
            val authAccountId = authServiceClient.createAccount(
                userId = userId,
                provider = command.provider,
                providerAccountId = command.providerAccountId,
                password = command.password,
            )

            // 3. User 활성화
            userServiceClient.activateUser(userId)

            // 4. 토큰 발급 (로그인 처리)
            val token = authServiceClient.issueToken(authAccountId)

            return SignUpResult(
                userId = userId,
                accessToken = token.accessToken,
                refreshToken = token.refreshToken,
            )
        } catch (ex: Exception) {
            // 5. 실패 시 보상 처리
            runCatching {
                userServiceClient.markSignupFailed(userId)
            }.onFailure {
                // TODO: 로그/메트릭 남김
                // TODO: 이후 배치/재처리 대상
            }
            throw ex
        }
    }
}
```

### BFF에서 사용
```kotlin
@RestController
@RequestMapping("/sign-up")
class SignUpController(
    private val signUpFacade: SignUpFacade,
) {
    @PostMapping("/local")
    suspend fun local(
        @Valid @RequestBody request: LocalSignUpRequest,
    ): ResponseEntity<SignUpResponse> {
        val result = signUpFacade.signUp(request.toCommand())

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(result.toResponse())
    }
}
```

## 트레이드오프

### 얻는 것
- 초기 개발 속도 향상
- 변경 시 한 곳만 수정 (응집도 증가)
- 인프라 복잡도 감소
- 사가 패턴 학습에 집중 가능

### 포기하는 것
- 책임 분리 원칙 일부 위반
- BFF의 복잡도 증가
- 독립적인 확장성 제한
- 사가 로직 재사용 어려움

### 향후 고려사항
- 도메인이 안정화되면 별도 오케스트레이터 서비스로 분리 검토
- 사가 플로우가 복잡해지거나 재사용이 필요해지면 분리
- 트래픽이 증가하여 독립적인 확장이 필요해지면 분리

## 분리 시점 판단 기준

다음 중 하나라도 해당되면 분리를 고려한다:

1. **사가 플로우 재사용**: 여러 BFF 또는 다른 서비스에서 동일한 사가 플로우 필요
2. **복잡도 임계점**: 사가 로직이 BFF 코드의 50% 이상 차지
3. **독립적 확장**: 사가 처리량과 BFF 처리량의 확장 요구사항이 다름
4. **도메인 안정화**: 서비스 호출 패턴이 3개월 이상 변경 없이 안정적
