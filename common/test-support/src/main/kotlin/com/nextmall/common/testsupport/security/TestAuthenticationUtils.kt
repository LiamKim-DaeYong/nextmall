package com.nextmall.common.testsupport.security

import com.nextmall.common.security.principal.AuthenticatedPrincipal
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder

/**
 * 테스트에서 인증된 사용자 컨텍스트를 설정하기 위한 유틸리티.
 *
 * 사용법:
 * ```
 * test("인증된 사용자로 테스트") {
 *     withAuthenticatedUser(userId = "user-123", roles = listOf("USER")) {
 *         // 인증된 상태에서 테스트 코드 실행
 *     }
 * }
 * ```
 */
object TestAuthenticationUtils {
    /**
     * 인증된 사용자 컨텍스트에서 코드 블록을 실행합니다.
     * 블록 실행 후 SecurityContext가 자동으로 정리됩니다.
     */
    inline fun <T> withAuthenticatedUser(
        userId: String = "1",
        roles: List<String> = listOf("USER"),
        block: () -> T,
    ): T {
        setAuthenticatedUser(userId, roles)
        return try {
            block()
        } finally {
            SecurityContextHolder.clearContext()
        }
    }

    /**
     * 인증된 사용자를 SecurityContext에 설정합니다.
     * 수동으로 clearContext()를 호출해야 합니다.
     */
    fun setAuthenticatedUser(
        userId: String = "1",
        roles: List<String> = listOf("USER"),
    ) {
        val authorities = roles.map { SimpleGrantedAuthority("ROLE_$it") }
        val principal =
            AuthenticatedPrincipal(
                subject = userId,
                userId = userId,
            )
        val authentication =
            UsernamePasswordAuthenticationToken(
                principal,
                null,
                authorities,
            ).apply {
                details = principal
            }
        SecurityContextHolder.getContext().authentication = authentication
    }

    /**
     * SecurityContext를 정리합니다.
     */
    fun clearContext() {
        SecurityContextHolder.clearContext()
    }
}

/**
 * 편의를 위한 최상위 함수.
 */
inline fun <T> withAuthenticatedUser(
    userId: String = "1",
    roles: List<String> = listOf("USER"),
    block: () -> T,
): T = TestAuthenticationUtils.withAuthenticatedUser(userId, roles, block)
