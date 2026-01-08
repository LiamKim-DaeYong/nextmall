package com.nextmall.common.policy.result

import com.nextmall.common.policy.model.Effect

/**
 * 정책 평가 결과.
 *
 * @property decision 최종 결정 (ALLOW/DENY)
 * @property reason 결정 사유
 * @property matchedRuleId 매칭된 규칙 ID (있을 경우)
 * @property matchedRuleName 매칭된 규칙 이름 (있을 경우)
 * @property policyId 평가된 정책 ID
 * @property policyVersion 평가된 정책 버전
 */
data class PolicyResult(
    val decision: Decision,
    val reason: String,
    val matchedRuleId: String? = null,
    val matchedRuleName: String? = null,
    val policyId: String,
    val policyVersion: Long,
) {
    val isAllowed: Boolean
        get() = decision == Decision.ALLOW

    val isDenied: Boolean
        get() = decision == Decision.DENY

    companion object {
        fun allow(
            policyId: String,
            policyVersion: Long,
            rule: RuleMatch,
        ): PolicyResult =
            PolicyResult(
                decision = Decision.ALLOW,
                reason = "규칙 '${rule.name}'에 의해 허용됨",
                matchedRuleId = rule.id,
                matchedRuleName = rule.name,
                policyId = policyId,
                policyVersion = policyVersion,
            )

        fun deny(
            policyId: String,
            policyVersion: Long,
            rule: RuleMatch,
        ): PolicyResult =
            PolicyResult(
                decision = Decision.DENY,
                reason = "규칙 '${rule.name}'에 의해 거부됨",
                matchedRuleId = rule.id,
                matchedRuleName = rule.name,
                policyId = policyId,
                policyVersion = policyVersion,
            )

        fun denyByDefault(
            policyId: String,
            policyVersion: Long,
        ): PolicyResult =
            PolicyResult(
                decision = Decision.DENY,
                reason = "매칭되는 ALLOW 규칙 없음 (기본 거부)",
                matchedRuleId = null,
                matchedRuleName = null,
                policyId = policyId,
                policyVersion = policyVersion,
            )

        fun denyPolicyDisabled(
            policyId: String,
            policyVersion: Long,
        ): PolicyResult =
            PolicyResult(
                decision = Decision.DENY,
                reason = "정책이 비활성화됨",
                matchedRuleId = null,
                matchedRuleName = null,
                policyId = policyId,
                policyVersion = policyVersion,
            )
    }
}

enum class Decision {
    ALLOW,
    DENY,
}

/**
 * 매칭된 규칙 정보.
 */
data class RuleMatch(
    val id: String,
    val name: String,
    val effect: Effect,
)
