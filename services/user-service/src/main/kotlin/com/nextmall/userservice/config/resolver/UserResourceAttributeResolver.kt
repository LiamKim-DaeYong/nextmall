package com.nextmall.userservice.config.resolver

import com.nextmall.common.authorization.aop.ResourceAttributeResolver
import org.aspectj.lang.ProceedingJoinPoint
import org.springframework.stereotype.Component

/**
 * User 리소스 속성 해석기.
 *
 * resourceId를 resource.id로 매핑하여 "본인 조회/수정" 정책 평가에 사용한다.
 */
@Component
class UserResourceAttributeResolver : ResourceAttributeResolver {
    override fun supports(resource: String): Boolean = resource == "user"

    override fun resolve(resourceId: String?, joinPoint: ProceedingJoinPoint): Map<String, Any> =
        resourceId?.let {
            mapOf("id" to it)
        } ?: emptyMap()
}
