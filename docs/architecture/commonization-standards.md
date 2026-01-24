# 공통화 및 컨벤션 기준 (초안)

본 문서는 서비스 간 일관성, 계약 안정성, 운영 품질을 확보하기 위한 공통화/컨벤션 기준 초안이다.
병렬 개발 속도를 유지하되, 실무 기준의 품질을 유지하기 위한 최소 규칙을 정의한다.

---

## 1. 목표
- 서비스 간 API 계약과 DTO 구조의 일관성 확보
- 공통 도메인 타입(돈, ID, 에러, 보안) 표준화
- 검증/에러 응답의 예측 가능성 확보
- 변경 시 회귀와 운영 리스크 최소화

## 2. 범위
- 외부 노출 API(BFF 포함)
- 서비스 간 통신 DTO 및 에러 계약
- 공통 라이브러리(보안, 예외, 유틸)

## 3. 공통 모듈 원칙
- 공통 타입은 `common-*` 모듈로 이동
- 공통 모듈은 도메인 서비스에 의존하지 않는다 (단방향)
- 공통 모듈은 기능이 아니라 “계약”과 “규칙”에 집중한다

권장 공통 모듈 (초안):
- `common-api`: 외부 공개 DTO, ErrorResponse, ErrorCode, Money, ID 타입
- `common-validation`: 표준 제약 애너테이션 조합/메시지
- `common-security`: CurrentUser, principal, token props, 기본 보안 설정
- `common-web`: 공통 예외 핸들러 베이스, 표준 응답 래퍼 (선택)

## 4. API / DTO 규칙
- 패키지 구조: `presentation/request`, `presentation/response`, `presentation/controller`
- DTO 네이밍: `*Request`, `*Response`, `*ViewResponse`
- 내부/외부 구분: 외부 공개 DTO는 `common-api`에 두고 서비스에서는 재사용
- 도메인 모델을 API 응답으로 직접 노출하지 않는다

## 5. 검증 규칙
- 모든 외부 요청 DTO는 `@Valid`를 통해 진입 검증
- 숫자: `@Positive` 또는 `@Min(1)` 명시
- 문자열: `@NotBlank` + 길이 제한 `@Size`
- enum/코드값: 허용 리스트를 명시하거나 커스텀 validator 적용

## 6. 에러 응답 규칙
- ErrorResponse는 단일 포맷으로 통일
- ErrorCode는 범주(category) 기반으로 HttpStatus 기본 매핑 유지
- 서비스별 예외 매핑은 공통 규칙을 우선하고 예외는 ADR로 기록

## 7. JSON / HTTP 규칙
- JSON 네이밍 전략은 전 서비스 동일 (camelCase 또는 snake_case 단일 선택)
- HTTP status는 의미에 맞게 일관되게 사용 (특히 auth/validation)
- 경로 스타일은 단일 규칙 유지 (kebab-case 권장)

## 8. 변경 정책
- 공통 DTO/에러/보안 변경은 변경 이력(CHANGELOG)과 함께 PR에 명시
- 깨지는 변경은 버전 분기 또는 변환 계층 추가
- 서비스별 임시 편차는 TODO가 아니라 ADR 또는 문서에 기록

## 9. 품질 기준 (실무 기준)
- 컨벤션 위반은 리팩터링 이슈로 등록
- 계약 변경 시 양쪽 서비스 동시 수정 원칙
- 신규 서비스는 공통 모듈 기준을 기본값으로 채택

---

## 실행 체크리스트 (요약)
- API DTO 위치/네이밍 확인
- 공통 타입 재사용 여부 확인
- Request 검증 규칙 적용 여부 확인
- ErrorResponse/HttpStatus 일관성 확인
- JSON 네이밍 전략 일치 여부 확인
