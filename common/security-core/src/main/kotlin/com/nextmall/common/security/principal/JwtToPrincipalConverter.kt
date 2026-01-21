package com.nextmall.common.security.principal

import org.springframework.security.oauth2.jwt.Jwt

/**
 * JWT 기반 인증에서 Jwt를 AuthenticatedPrincipal로 변환한다.
 *
 * 이 구현체는 JWT claim 중
 * - subject
 * - userId
 * 만을 사용하여 인증된 사용자 식별 정보를 생성한다.
 */
class JwtToPrincipalConverter : AuthenticationTokenToPrincipalConverter<Jwt> {
    override fun convert(token: Jwt): AuthenticatedPrincipal {
        val subject =
            token.subject
                ?: throw IllegalArgumentException("JWT subject(claim 'sub')가 존재하지 않습니다.")

        val userId =
            token.getClaimAsString("user_id")
                ?: throw IllegalArgumentException("JWT claim 'user_id'가 존재하지 않습니다.")

        return AuthenticatedPrincipal(
            subject = subject,
            userId = userId,
        )
    }
}
