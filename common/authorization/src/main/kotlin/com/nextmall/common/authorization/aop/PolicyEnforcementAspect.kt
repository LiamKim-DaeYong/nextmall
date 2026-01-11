package com.nextmall.common.authorization.aop

import com.nextmall.common.authorization.annotation.RequiresPolicy
import com.nextmall.common.authorization.context.AuthorizationContext
import com.nextmall.common.authorization.exception.AccessDeniedException
import com.nextmall.common.authorization.service.AuthorizationService
import com.nextmall.common.security.principal.AuthenticatedPrincipal
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder

/**
 * 정책 기반 인가 AOP Aspect.
 *
 * @RequiresPolicy 어노테이션이 붙은 메서드에 대해
 * 호출 전 인가를 평가하고, 거부 시 예외를 발생시킨다.
 */
@Aspect
class PolicyEnforcementAspect(
    private val authorizationService: AuthorizationService,
    private val resourceAttributeResolvers: List<ResourceAttributeResolver> = emptyList(),
) {
    @Around("@annotation(requiresPolicy)")
    fun enforcePolicy(
        joinPoint: ProceedingJoinPoint,
        requiresPolicy: RequiresPolicy,
    ): Any? {
        val context = buildAuthorizationContext(joinPoint, requiresPolicy)
        authorizationService.authorize(context)
        return joinPoint.proceed()
    }

    /**
     * 인가 컨텍스트를 구성한다.
     */
    private fun buildAuthorizationContext(
        joinPoint: ProceedingJoinPoint,
        annotation: RequiresPolicy,
    ): AuthorizationContext {
        val authentication = getAuthentication()
        val principal = extractPrincipal(authentication)
        val resourceId = extractResourceId(joinPoint, annotation)
        val resourceAttributes =
            resolveResourceAttributes(
                resource = annotation.resource,
                resourceId = resourceId,
                joinPoint = joinPoint,
            )

        return AuthorizationContext(
            userId = principal.userId,
            roles = extractRoles(authentication),
            resource = annotation.resource,
            resourceId = resourceId,
            resourceAttributes = resourceAttributes,
            action = annotation.action,
        )
    }

    /**
     * 현재 인증 객체를 가져온다.
     */
    private fun getAuthentication(): Authentication {
        val authentication =
            SecurityContextHolder.getContext().authentication
                ?: throw AccessDeniedException(
                    resource = "unknown",
                    action = "unknown",
                    reason = "Authentication required",
                )

        if (!authentication.isAuthenticated) {
            throw AccessDeniedException(
                resource = "unknown",
                action = "unknown",
                reason = "User is not authenticated",
            )
        }

        return authentication
    }

    /**
     * 인증 객체에서 사용자 정보를 추출한다.
     */
    private fun extractPrincipal(authentication: Authentication): AuthenticatedPrincipal =
        authentication.details as? AuthenticatedPrincipal
            ?: throw AccessDeniedException(
                resource = "unknown",
                action = "unknown",
                reason = "Invalid authentication principal",
            )

    /**
     * 인증 객체에서 사용자의 역할을 추출한다.
     *
     * TODO: AuthenticatedPrincipal에 roles 추가 후 수정 필요
     */
    private fun extractRoles(authentication: Authentication): Set<String> =
        authentication.authorities
            .mapNotNull { it.authority }
            .filter { it.startsWith("ROLE_") }
            .map { it.removePrefix("ROLE_") }
            .toSet()

    /**
     * 메서드 파라미터에서 리소스 ID를 추출한다.
     */
    private fun extractResourceId(
        joinPoint: ProceedingJoinPoint,
        annotation: RequiresPolicy,
    ): String? {
        if (annotation.resourceIdParam.isBlank()) return null

        val signature = joinPoint.signature as MethodSignature
        val paramNames = signature.parameterNames ?: return null
        val paramIndex = paramNames.indexOf(annotation.resourceIdParam)

        if (paramIndex < 0) return null

        return joinPoint.args[paramIndex]?.toString()
    }

    /**
     * 리소스 속성을 해석한다.
     */
    private fun resolveResourceAttributes(
        resource: String,
        resourceId: String?,
        joinPoint: ProceedingJoinPoint,
    ): Map<String, Any> {
        val resolver =
            resourceAttributeResolvers.find { it.supports(resource) }
                ?: return emptyMap()

        return resolver.resolve(resourceId, joinPoint)
    }
}

/**
 * 리소스 속성 해석기 인터페이스.
 *
 * 각 서비스에서 구현하여 리소스 ID로 추가 속성을 조회한다.
 * 예: OrderResourceAttributeResolver가 orderId로 주문 정보(ownerId, status 등)를 조회
 */
interface ResourceAttributeResolver {
    /**
     * 이 해석기가 지원하는 리소스 타입인지 확인한다.
     */
    fun supports(resource: String): Boolean

    /**
     * 리소스 속성을 해석한다.
     */
    fun resolve(resourceId: String?, joinPoint: ProceedingJoinPoint): Map<String, Any>
}
