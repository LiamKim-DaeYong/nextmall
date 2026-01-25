# ADR-001: JOOQ와 JPA 분리 전략

> QueryDSL의 KAPT 의존성 문제와 N+1 문제 완화를 위해 Command는 JPA, Query는 jOOQ로 분리 (CQRS)

## 상태
채택됨 (Accepted)

## 배경

### 문제 배경 (JPA + QueryDSL)

JPA와 QueryDSL을 사용할 때 다음과 같은 문제가 발생할 수 있다:

#### 1. Repository 복잡도 증가
- 단일 Repository에 Command와 Query 로직이 혼재
- 파일이 비대해지고 책임 구분이 모호해짐
- 중복 코드 발생

#### 2. N+1 문제가 지속적으로 발생할 수 있음
- 계층이 많아질수록 Lazy Loading으로 인한 쿼리 폭발
- 예: `in` 쿼리로 조회 → Mapper에서 연관 엔티티 접근 → N+1 발생
- Fetch Join으로 완화해도 Mapper 계층에서 다시 발생

#### 3. Command와 Query의 호출 패턴 차이
- Query: `Controller → QueryService` (단순 조회)
- Command: `Gateway → Handler → CommandService` (복잡한 비즈니스 로직)
- 같은 Repository를 공유하지만 사용 패턴이 완전히 다름

#### 4. 개선 시도와 한계
- QueryRepository 분리 → QueryDSL 기반 Projection 조회로 전환 중
- 하지만 근본적인 문제는 남을 수 있음

### Kotlin 생태계 문제

#### KAPT의 한계
- Kotlin 버전이 높아지면서 KAPT 지원 중단 예정
- KSP(Kotlin Symbol Processing)로 마이그레이션 권장

#### QueryDSL의 KSP 미지원
- KSP는 QueryDSL을 공식적으로 지원하지 않음
- 커뮤니티 플러그인은 불안정하고 유지보수 불확실

#### 대안: JOOQ
- KotlinGenerator로 Kotlin 코드 생성(빌드 시 codegen 방식)
- Type-safe SQL 작성 가능
- Kotlin과의 호환성이 상대적으로 유리

### 핵심 인사이트

#### Query와 Command는 역할이 다르다
- Query: 읽기 최적화, 성능 고려, Projection
- Command: 쓰기 최적화, 트랜잭션, 도메인 로직
- 같은 도구를 사용할 필요가 없음

## 대안 비교

| 구분 | JPA 단일 사용 | JPA + QueryDSL | JPA + JOOQ |
|------|--------------|----------------|------------|
| **Command 처리** | 상대적으로 유리 | 상대적으로 유리 | 상대적으로 유리 |
| **Query 처리** | 한계 (N+1) | 상대적으로 유리 | 상대적으로 유리 |
| **Kotlin 지원** | 보통 | KAPT 의존 | KSP 지원 |
| **Repository 복잡도** | 높음 | 높음 | 낮음 (분리) |
| **학습 곡선** | 낮음 | 중간 | 중간 |
| **타입 안정성** | 보통 | 높음 | 높음 |
| **미래 지속성** | 상대적으로 높음 | 불확실 (KAPT) | 상대적으로 높음 (KSP) |

### JPA 단일 사용을 선택하지 않은 이유
- N+1 문제 완화가 어려움
- 복잡한 조회 쿼리 작성 한계
- Projection 최적화 한계

### JPA + QueryDSL을 선택하지 않은 이유
- KAPT 의존성으로 인한 미래 불확실성
- KSP 마이그레이션 시 QueryDSL 사용 불가
- Repository 복잡도 문제 여전히 존재

## 결정

**Command는 JPA, Query는 JOOQ**로 분리하여 사용한다.

### 핵심 선택 이유

#### 1. CQRS 패턴 적용
- Command: JPA로 도메인 모델 중심 처리
- Query: JOOQ로 조회 성능 고려
- 각 역할에 최적화된 도구 사용

#### 2. Kotlin 생태계 대응
- JOOQ는 KSP를 공식 지원
- Kotlin 버전 업그레이드에 안전
- 장기적인 유지보수 가능

#### 3. Repository 책임 분리
- CommandRepository (JPA): 엔티티 저장, 수정, 삭제
- QueryRepository (JOOQ): 복잡한 조회, Projection
- 파일 크기 감소, 책임 구분에 도움

#### 4. N+1 문제 완화
- JOOQ는 명시적 SQL 작성
- Lazy Loading 없음
- 필요한 데이터만 정확히 조회

## 구현 방식

### Command (JPA)
```kotlin
@Entity
@Table(name = "users")
class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    var nickname: String,
    var status: UserStatus,
)

interface UserCommandRepository : JpaRepository<User, Long> {
    // 간단한 Command 메서드만 정의
}

@Service
class UserCommandService(
    private val userCommandRepository: UserCommandRepository
) {
    @Transactional
    fun createUser(nickname: String): Long {
        val user = User(nickname = nickname, status = UserStatus.PENDING)
        return userCommandRepository.save(user).id
    }
    
    @Transactional
    fun activateUser(userId: Long) {
        val user = userCommandRepository.findById(userId)
            .orElseThrow { UserNotFoundException() }
        user.status = UserStatus.ACTIVE
    }
}
```

### Query (JOOQ)
```kotlin
data class UserDetailProjection(
    val id: Long,
    val nickname: String,
    val email: String?,
    val createdAt: Instant,
    val orderCount: Int,
)

@Repository
class UserQueryRepository(
    private val dsl: DSLContext
) {
    fun findUserDetail(userId: Long): UserDetailProjection? {
        return dsl
            .select(
                USERS.ID,
                USERS.NICKNAME,
                USERS.EMAIL,
                USERS.CREATED_AT,
                count(ORDERS.ID).`as`("orderCount")
            )
            .from(USERS)
            .leftJoin(ORDERS).on(ORDERS.USER_ID.eq(USERS.ID))
            .where(USERS.ID.eq(userId))
            .groupBy(USERS.ID)
            .fetchOne { record ->
                UserDetailProjection(
                    id = record[USERS.ID],
                    nickname = record[USERS.NICKNAME],
                    email = record[USERS.EMAIL],
                    createdAt = record[USERS.CREATED_AT],
                    orderCount = record["orderCount", Int::class.java],
                )
            }
    }
}

@Service
class UserQueryService(
    private val userQueryRepository: UserQueryRepository
) {
    fun getUserDetail(userId: Long): UserDetailProjection {
        return userQueryRepository.findUserDetail(userId)
            ?: throw UserNotFoundException()
    }
}
```

## 트레이드오프

### 얻는 것
- CQRS 패턴으로 책임 구분에 도움
- N+1 문제 완화
- Kotlin 생태계 변화에 안전 (KSP)
- Repository 복잡도 감소
- 각 역할에 최적화된 도구 사용

### 포기하는 것
- 두 가지 기술 스택 학습 필요
- 초기 설정 복잡도 증가
- 일부 코드 중복 가능성 (엔티티 vs DTO)

### 리스크 완화
- JPA와 JOOQ 모두 성숙한 기술
- 사용 기준 문서화 (Command는 JPA, Query는 JOOQ)

## 사용 기준

### JPA를 사용하는 경우
- 엔티티 생성, 수정, 삭제
- 트랜잭션 내 도메인 로직 처리
- 간단한 단건 조회 (findById)

### JOOQ를 사용하는 경우
- 복잡한 조인 쿼리
- 집계 함수 사용
- Projection이 필요한 조회
- 성능이 중요한 대량 조회
- 동적 쿼리 작성

## jOOQ 생성 코드 Git 관리

### 왜 .gitignore에 넣지 않았나?

일반적으로 생성 코드는 Git에서 제외하지만, jOOQ 생성 코드는 의도적으로 버전 관리한다.

| 구분 | .gitignore | Git 관리 (선택) |
|------|------------|----------------|
| CI 빌드 | DB 컨테이너 필요 | DB 불필요 |
| IDE 지원 | 빌드 후 사용 가능 | 클론 즉시 자동완성 |
| 스키마 변경 | 암묵적 | PR에서 명시적 확인 |
| 빌드 시간 | 매번 생성 | 생성 단계 생략 |

### 핵심 이유

#### 1. CI/CD 단순화
- 빌드 시 DB 컨테이너 불필요
- GitHub Actions에서 PostgreSQL 설정 생략
- 빌드 시간 단축

#### 2. 개발 편의성
- `git clone` 후 바로 IDE 자동완성 동작
- 신규 팀원 온보딩 시 추가 설정 불필요

#### 3. 스키마 변경 가시성
- PR에서 테이블/컬럼 변경이 명시적으로 드러남
- 코드 리뷰 시 스키마 변경 영향도 파악 용이
