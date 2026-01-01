package com.nextmall.common.security.spring

import org.springframework.security.core.annotation.AuthenticationPrincipal

/**
 * 현재 인증된 사용자를 나타내는 파라미터 어노테이션.
 *
 * 내부적으로는 Spring Security의 @AuthenticationPrincipal을 사용하며,
 * 인증(AuthN) 결과 객체(AuthenticatedPrincipal)를 주입받는다.
 *
 * 이 어노테이션은 인가(AuthZ) 책임을 포함하지 않는다.
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@AuthenticationPrincipal
annotation class CurrentUser(
    val required: Boolean = true
)
