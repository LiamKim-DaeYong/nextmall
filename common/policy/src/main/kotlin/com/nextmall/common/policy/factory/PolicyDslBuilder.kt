package com.nextmall.common.policy.factory

import com.nextmall.common.policy.model.Condition
import com.nextmall.common.policy.model.Effect
import com.nextmall.common.policy.model.Operator
import com.nextmall.common.policy.model.Policy
import com.nextmall.common.policy.model.Rule

/**
 * Policy DSL 진입점.
 *
 * 사용 예시:
 * ```
 * val policy = policy("user", "read") {
 *     name = "사용자 조회 정책"
 *     description = "사용자 정보 조회 권한 정책"
 *
 *     allow("관리자 전체 조회") {
 *         condition("subject.roles", Operator.CONTAINS, "ADMIN")
 *     }
 *
 *     allow("본인 정보 조회") {
 *         conditionRef("subject.userId", Operator.EQUALS, "resource.id")
 *     }
 *
 *     deny("탈퇴 회원 접근 차단") {
 *         condition("subject.status", Operator.EQUALS, "WITHDRAWN")
 *     }
 * }
 * ```
 */
fun policy(
    resource: String,
    action: String,
    block: PolicyBuilder.() -> Unit,
): Policy = PolicyBuilder(resource, action).apply(block).build()

@DslMarker
annotation class PolicyDsl

@PolicyDsl
class PolicyBuilder(
    private val resource: String,
    private val action: String,
) {
    var name: String = "$resource:$action"
    var description: String? = null
    var enabled: Boolean = true

    private val rules = mutableListOf<Rule>()
    private var ruleCounter = 0

    fun allow(description: String, block: ConditionBuilder.() -> Unit) {
        addRule(Effect.ALLOW, description, block)
    }

    fun deny(description: String, block: ConditionBuilder.() -> Unit) {
        addRule(Effect.DENY, description, block)
    }

    private fun addRule(effect: Effect, description: String, block: ConditionBuilder.() -> Unit) {
        val conditions = ConditionBuilder().apply(block).build()
        val rule =
            Rule(
                id = generateRuleId(),
                name = description,
                description = description,
                effect = effect,
                conditions = conditions,
                priority = ruleCounter,
            )
        rules.add(rule)
        ruleCounter++
    }

    private fun generateRuleId(): String = "$resource:$action:rule-$ruleCounter"

    fun build(): Policy =
        Policy(
            id = "$resource:$action",
            name = name,
            description = description,
            resource = resource,
            action = action,
            rules = rules.toList(),
            enabled = enabled,
        )
}

@PolicyDsl
class ConditionBuilder {
    private val conditions = mutableListOf<Condition>()

    /**
     * 고정값과 비교하는 조건 추가.
     *
     * @param field 비교할 필드 경로 (예: "subject.roles", "resource.amount")
     * @param operator 비교 연산자
     * @param value 비교할 고정값
     */
    fun condition(field: String, operator: Operator, value: Any) {
        conditions.add(
            Condition(
                field = field,
                operator = operator,
                value = value,
            ),
        )
    }

    /**
     * 다른 필드 값을 참조해서 비교하는 조건 추가.
     * subject.userId == resource.ownerId 같은 동적 비교에 사용.
     *
     * @param field 비교할 필드 경로
     * @param operator 비교 연산자
     * @param refPath 비교할 참조 경로 (예: "resource.ownerId")
     */
    fun conditionRef(field: String, operator: Operator, refPath: String) {
        conditions.add(
            Condition(
                field = field,
                operator = operator,
                valueRef = refPath,
            ),
        )
    }

    fun build(): List<Condition> {
        require(conditions.isNotEmpty()) {
            "규칙에는 최소 하나의 조건이 필요합니다"
        }
        return conditions.toList()
    }
}
