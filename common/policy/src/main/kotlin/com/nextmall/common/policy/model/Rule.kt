package com.nextmall.common.policy.model

/**
 * 정책 규칙.
 * 여러 조건(conditions)을 AND로 조합하여 평가한다.
 * 모든 조건이 충족되면 effect(ALLOW/DENY)를 반환한다.
 *
 * @property id 규칙 고유 식별자
 * @property name 규칙 이름 (로깅/디버깅용)
 * @property description 규칙 설명
 * @property effect 조건 충족 시 효과 (ALLOW/DENY)
 * @property conditions 조건 목록 (AND 조합)
 * @property priority 우선순위 (낮을수록 먼저 평가, 기본값 0)
 */
data class Rule(
    val id: String,
    val name: String,
    val description: String? = null,
    val effect: Effect,
    val conditions: List<Condition>,
    val priority: Int = 0,
) {
    init {
        require(conditions.isNotEmpty()) {
            "규칙에는 최소 하나의 조건이 필요합니다"
        }
    }
}
