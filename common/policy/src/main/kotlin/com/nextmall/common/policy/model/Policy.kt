package com.nextmall.common.policy.model

/**
 * 정책.
 * 특정 리소스와 액션에 대한 접근 제어 규칙의 집합.
 *
 * 평가 방식 (DENY 우선):
 * 1. DENY 규칙 먼저 평가 - 하나라도 매칭되면 즉시 거부
 * 2. ALLOW 규칙 평가 - 하나라도 매칭되면 허용
 * 3. 아무것도 매칭되지 않으면 기본 거부
 *
 * @property id 정책 고유 식별자
 * @property name 정책 이름
 * @property description 정책 설명
 * @property resource 대상 리소스 타입 (예: "settlement", "product", "order")
 * @property action 대상 액션 (예: "read", "create", "update", "approve")
 * @property rules 규칙 목록
 * @property enabled 정책 활성화 여부
 * @property version 정책 버전 (변경 추적용)
 */
data class Policy(
    val id: String,
    val name: String,
    val description: String? = null,
    val resource: String,
    val action: String,
    val rules: List<Rule>,
    val enabled: Boolean = true,
    val version: Long = 1,
) {
    init {
        require(rules.isNotEmpty()) {
            "정책에는 최소 하나의 규칙이 필요합니다"
        }
    }

    /**
     * DENY 규칙 목록 (priority 오름차순 정렬)
     */
    val denyRules: List<Rule>
        get() = rules.filter { it.effect == Effect.DENY }.sortedBy { it.priority }

    /**
     * ALLOW 규칙 목록 (priority 오름차순 정렬)
     */
    val allowRules: List<Rule>
        get() = rules.filter { it.effect == Effect.ALLOW }.sortedBy { it.priority }
}
