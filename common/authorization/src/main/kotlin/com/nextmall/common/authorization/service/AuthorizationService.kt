package com.nextmall.common.authorization.service

import com.nextmall.common.authorization.context.AuthorizationContext
import com.nextmall.common.authorization.exception.AccessDeniedException
import com.nextmall.common.authorization.exception.PolicyNotFoundException
import com.nextmall.common.policy.context.PolicyContext
import com.nextmall.common.policy.context.Resource
import com.nextmall.common.policy.context.Subject
import com.nextmall.common.policy.evaluator.PolicyEvaluator
import com.nextmall.common.policy.model.Policy
import com.nextmall.common.policy.result.PolicyResult

/**
 * 인가 서비스.
 *
 * PolicyEvaluator를 사용하여 인가를 평가하고,
 * 결과에 따라 허용하거나 예외를 발생시킨다.
 */
class AuthorizationService(
    private val policyProvider: PolicyProvider,
    private val policyEvaluator: PolicyEvaluator,
    private val auditLogger: AuthorizationAuditLogger? = null,
) {
    /**
     * 인가를 평가한다.
     *
     * @param context 인가 컨텍스트
     * @return 평가 결과
     * @throws PolicyNotFoundException 정책을 찾을 수 없는 경우
     */
    fun evaluate(context: AuthorizationContext): PolicyResult {
        val policy =
            policyProvider.getPolicy(context.resource, context.action)
                ?: throw PolicyNotFoundException(context.resource, context.action)

        val policyContext = buildPolicyContext(context)
        val result = policyEvaluator.evaluate(policy, policyContext)

        // 감사 로깅 (비동기)
        auditLogger?.log(context, result)

        return result
    }

    /**
     * 인가를 평가하고, 거부 시 예외를 발생시킨다.
     *
     * @param context 인가 컨텍스트
     * @throws AccessDeniedException 인가가 거부된 경우
     * @throws PolicyNotFoundException 정책을 찾을 수 없는 경우
     */
    fun authorize(context: AuthorizationContext) {
        val result = evaluate(context)

        if (result.isDenied) {
            throw AccessDeniedException(
                resource = context.resource,
                action = context.action,
                reason = result.reason,
            )
        }
    }

    /**
     * AuthorizationContext를 PolicyContext로 변환한다.
     */
    private fun buildPolicyContext(context: AuthorizationContext): PolicyContext =
        PolicyContext(
            subject =
                Subject(
                    userId = context.userId,
                    roles = context.roles,
                    attributes = context.userAttributes,
                ),
            resource =
                Resource(
                    type = context.resource,
                    id = context.resourceId,
                    attributes = context.resourceAttributes,
                ),
            action = context.action,
        )
}

/**
 * 정책 제공자 인터페이스.
 *
 * 정책을 조회하는 방법을 추상화한다.
 * 구현체: CachedPolicyProvider (Redis 캐시 + API 호출)
 */
interface PolicyProvider {
    /**
     * 리소스와 액션에 해당하는 정책을 조회한다.
     *
     * @param resource 리소스 타입
     * @param action 액션
     * @return 정책 (없으면 null)
     */
    fun getPolicy(resource: String, action: String): Policy?
}

/**
 * 인가 감사 로거 인터페이스.
 */
interface AuthorizationAuditLogger {
    fun log(context: AuthorizationContext, result: PolicyResult)
}
