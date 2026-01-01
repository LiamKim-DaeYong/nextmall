package com.nextmall.common.security.spring

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint

/**
 * 인증(AuthN) 실패 시 HTTP 401 응답을 반환한다.
 *
 * 이 EntryPoint는 인증 실패의 "표현 방식"만 책임지며,
 * 에러 코드, 메시지, i18n 등은 이후 단계에서 확장한다.
 */
class JwtAuthenticationEntryPoint : AuthenticationEntryPoint {

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        response.status = HttpServletResponse.SC_UNAUTHORIZED
    }
}
