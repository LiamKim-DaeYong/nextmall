package com.nextmall.common.policy.context

import java.time.Instant

/**
 * 정책 평가 컨텍스트.
 * 정책 평가에 필요한 모든 정보를 담는다.
 *
 * @property subject 요청 주체 정보 (사용자)
 * @property resource 대상 리소스 정보
 * @property action 수행할 액션
 * @property environment 환경 정보 (시간, IP 등)
 */
data class PolicyContext(
    val subject: Subject,
    val resource: Resource,
    val action: String,
    val environment: Environment = Environment(),
) {
    /**
     * 필드 경로로 값을 조회한다.
     * 예: "subject.roles", "resource.amount", "environment.timestamp"
     */
    fun getValue(fieldPath: String): Any? {
        val parts = fieldPath.split(".", limit = 2)
        if (parts.size < 2) return null

        val (root, path) = parts
        return when (root) {
            "subject" -> subject.getAttribute(path)
            "resource" -> resource.getAttribute(path)
            "environment" -> environment.getAttribute(path)
            else -> null
        }
    }
}

/**
 * 요청 주체 (사용자) 정보.
 *
 * @property userId 사용자 ID
 * @property roles 사용자 역할 목록
 * @property attributes 추가 속성 (assignedSellers, approvalLimit 등)
 */
data class Subject(
    val userId: String,
    val roles: Set<String> = emptySet(),
    val attributes: Map<String, Any> = emptyMap(),
) {
    fun getAttribute(key: String): Any? =
        when (key) {
            "userId" -> userId
            "roles" -> roles
            else -> attributes[key]
        }
}

/**
 * 대상 리소스 정보.
 *
 * @property type 리소스 타입 (예: "settlement", "product")
 * @property id 리소스 ID
 * @property attributes 추가 속성 (sellerId, amount, categoryId 등)
 */
data class Resource(
    val type: String,
    val id: String? = null,
    val attributes: Map<String, Any> = emptyMap(),
) {
    fun getAttribute(key: String): Any? =
        when (key) {
            "type" -> type
            "id" -> id
            else -> attributes[key]
        }
}

/**
 * 환경 정보.
 *
 * @property timestamp 요청 시각
 * @property attributes 추가 속성 (ip, userAgent 등)
 */
data class Environment(
    val timestamp: Instant = Instant.now(),
    val attributes: Map<String, Any> = emptyMap(),
) {
    fun getAttribute(key: String): Any? =
        when (key) {
            "timestamp" -> timestamp
            else -> attributes[key]
        }
}
