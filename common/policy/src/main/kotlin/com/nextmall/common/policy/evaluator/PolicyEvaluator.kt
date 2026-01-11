package com.nextmall.common.policy.evaluator

import com.nextmall.common.policy.context.PolicyContext
import com.nextmall.common.policy.model.Condition
import com.nextmall.common.policy.model.Effect
import com.nextmall.common.policy.model.Operator
import com.nextmall.common.policy.model.Policy
import com.nextmall.common.policy.model.Rule
import com.nextmall.common.policy.result.PolicyResult
import com.nextmall.common.policy.result.RuleMatch

/**
 * 정책 평가기.
 * 정책과 컨텍스트를 받아 접근 허용/거부를 결정한다.
 *
 * 평가 순서:
 * 1. 정책 비활성화 여부 확인
 * 2. DENY 규칙 평가 (하나라도 매칭되면 즉시 거부)
 * 3. ALLOW 규칙 평가 (하나라도 매칭되면 허용)
 * 4. 매칭되는 규칙 없으면 기본 거부
 */
class PolicyEvaluator {
    /**
     * 정책을 평가하여 결과를 반환한다.
     */
    fun evaluate(
        policy: Policy,
        context: PolicyContext,
    ): PolicyResult {
        // 정책 비활성화 체크
        if (!policy.enabled) {
            return PolicyResult.denyPolicyDisabled(policy.id, policy.version)
        }

        // 1. DENY 규칙 먼저 평가
        for (rule in policy.denyRules) {
            if (evaluateRule(rule, context)) {
                return PolicyResult.deny(
                    policyId = policy.id,
                    policyVersion = policy.version,
                    rule = RuleMatch(rule.id, rule.name, Effect.DENY),
                )
            }
        }

        // 2. ALLOW 규칙 평가
        for (rule in policy.allowRules) {
            if (evaluateRule(rule, context)) {
                return PolicyResult.allow(
                    policyId = policy.id,
                    policyVersion = policy.version,
                    rule = RuleMatch(rule.id, rule.name, Effect.ALLOW),
                )
            }
        }

        // 3. 기본 거부
        return PolicyResult.denyByDefault(policy.id, policy.version)
    }

    /**
     * 모든 매칭되는 규칙을 수집한다. (비즈니스 로직용)
     *
     * @param policy 정책
     * @param context 평가 컨텍스트
     * @return 매칭된 규칙 목록
     */
    fun evaluateAll(
        policy: Policy,
        context: PolicyContext,
    ): List<RuleMatch> {
        if (!policy.enabled) return emptyList()

        return policy.rules
            .filter { evaluateRule(it, context) }
            .map { RuleMatch(it.id, it.name, it.effect) }
    }

    /**
     * 규칙의 모든 조건을 평가한다 (AND 조합).
     */
    private fun evaluateRule(
        rule: Rule,
        context: PolicyContext,
    ): Boolean = rule.conditions.all { evaluateCondition(it, context) }

    /**
     * 단일 조건을 평가한다.
     */
    private fun evaluateCondition(
        condition: Condition,
        context: PolicyContext,
    ): Boolean {
        val fieldValue = context.getValue(condition.field)
        val compareValue =
            if (condition.valueRef != null) {
                context.getValue(condition.valueRef)
            } else {
                condition.value
            }

        return evaluateOperator(condition.operator, fieldValue, compareValue)
    }

    /**
     * 연산자에 따라 두 값을 비교한다.
     */
    @Suppress("UNCHECKED_CAST")
    private fun evaluateOperator(
        operator: Operator,
        fieldValue: Any?,
        compareValue: Any?,
    ): Boolean =
        when (operator) {
            Operator.EQUALS -> fieldValue == compareValue

            Operator.NOT_EQUALS -> fieldValue != compareValue

            Operator.IN -> {
                val collection = compareValue as? Collection<*> ?: return false
                fieldValue in collection
            }

            Operator.NOT_IN -> {
                val collection = compareValue as? Collection<*> ?: return true
                fieldValue !in collection
            }

            Operator.CONTAINS -> {
                val collection = fieldValue as? Collection<*> ?: return false
                compareValue in collection
            }

            Operator.NOT_CONTAINS -> {
                val collection = fieldValue as? Collection<*> ?: return true
                compareValue !in collection
            }

            Operator.GREATER_THAN -> compareNumbers(fieldValue, compareValue) { a, b -> a > b }

            Operator.GREATER_THAN_OR_EQUAL -> compareNumbers(fieldValue, compareValue) { a, b -> a >= b }

            Operator.LESS_THAN -> compareNumbers(fieldValue, compareValue) { a, b -> a < b }

            Operator.LESS_THAN_OR_EQUAL -> compareNumbers(fieldValue, compareValue) { a, b -> a <= b }
        }

    /**
     * 숫자 비교를 수행한다.
     */
    private fun compareNumbers(
        fieldValue: Any?,
        compareValue: Any?,
        comparison: (Double, Double) -> Boolean,
    ): Boolean {
        val fieldNumber = toDouble(fieldValue) ?: return false
        val compareNumber = toDouble(compareValue) ?: return false
        return comparison(fieldNumber, compareNumber)
    }

    private fun toDouble(value: Any?): Double? =
        when (value) {
            is Number -> value.toDouble()
            is String -> value.toDoubleOrNull()
            else -> null
        }
}
