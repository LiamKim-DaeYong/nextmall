package com.nextmall.common.security.spring

import com.nextmall.common.security.principal.AuthenticatedPrincipal
import org.springframework.core.MethodParameter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

/**
 * @CurrentUser 어노테이션을 처리하는 ArgumentResolver (WebMvc용).
 *
 * SecurityContext의 Authentication.details에서 AuthenticatedPrincipal을 추출한다.
 */
class CurrentUserArgumentResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean =
        parameter.hasParameterAnnotation(CurrentUser::class.java)

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): AuthenticatedPrincipal? {
        val annotation = parameter.getParameterAnnotation(CurrentUser::class.java)!!
        val authentication = SecurityContextHolder.getContext().authentication

        val principal = authentication?.details as? AuthenticatedPrincipal

        if (principal == null && annotation.required) {
            throw IllegalStateException(
                "인증된 사용자 정보가 필요합니다. " +
                    "인증되지 않은 요청을 허용하려면 @CurrentUser(required = false)를 사용하세요.",
            )
        }

        return principal
    }
}
