package com.nextmall.common.authorization.context

/**
 * 인가 평가에 필요한 컨텍스트 정보.
 *
 * PolicyContext로 변환되어 PolicyEvaluator에 전달된다.
 */
data class AuthorizationContext(
    /** 사용자 ID */
    val userId: String,

    /** 사용자 역할 목록 */
    val roles: Set<String>,

    /** 사용자 추가 속성 (예: 담당 셀러 목록, 승인 한도 등) */
    val userAttributes: Map<String, Any> = emptyMap(),

    /** 리소스 타입 (예: "order", "product") */
    val resource: String,

    /** 리소스 ID (선택적) */
    val resourceId: String? = null,

    /** 리소스 추가 속성 (예: 소유자 ID, 상태, 금액 등) */
    val resourceAttributes: Map<String, Any> = emptyMap(),

    /** 액션 (예: "read", "update", "approve") */
    val action: String,
)
