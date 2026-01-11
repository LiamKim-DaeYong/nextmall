package com.nextmall.common.security.spring

import com.nextmall.common.security.internal.ServiceTokenIssuer
import com.nextmall.common.security.principal.AuthenticatedPrincipal
import com.nextmall.common.security.principal.ServicePrincipal
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter

/**
 * 서비스 토큰을 Authentication으로 변환.
 *
 * - 사용자 정보(user_id, roles)가 있으면: AuthenticatedPrincipal 생성 (사용자 대리 요청)
 * - 없으면: ServicePrincipal 생성 (순수 서비스 간 통신)
 */
class ServiceJwtAuthenticationConverter : Converter<Jwt, AbstractAuthenticationToken> {
    private val authoritiesConverter = JwtGrantedAuthoritiesConverter()

    override fun convert(jwt: Jwt): AbstractAuthenticationToken {
        val serviceName =
            jwt.subject
                ?: throw IllegalArgumentException("JWT subject(service name)가 존재하지 않습니다.")

        val userId = jwt.getClaimAsString(ServiceTokenIssuer.USER_ID_CLAIM)
        val roles = extractRoles(jwt)

        val authorities = buildAuthorities(jwt, roles)

        return JwtAuthenticationToken(jwt, authorities, serviceName).apply {
            details =
                if (userId != null) {
                    // 사용자 컨텍스트가 있는 요청
                    AuthenticatedPrincipal(
                        subject = userId,
                        userId = userId,
                    )
                } else {
                    // 순수 서비스 간 통신
                    val scope = jwt.getClaimAsString("scope") ?: ""
                    ServicePrincipal(
                        serviceName = serviceName,
                        scope = scope,
                    )
                }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun extractRoles(jwt: Jwt): Set<String> {
        val rolesClaim = jwt.getClaim<Any>(ServiceTokenIssuer.ROLES_CLAIM) ?: return emptySet()
        return when (rolesClaim) {
            is List<*> -> rolesClaim.filterIsInstance<String>().toSet()
            is Collection<*> -> (rolesClaim as Collection<String>).toSet()
            else -> emptySet()
        }
    }

    private fun buildAuthorities(jwt: Jwt, roles: Set<String>): Collection<SimpleGrantedAuthority> {
        val scopeAuthorities = authoritiesConverter.convert(jwt) ?: emptyList()
        val roleAuthorities = roles.map { SimpleGrantedAuthority("ROLE_$it") }
        return scopeAuthorities.mapNotNull { it.authority?.let { auth -> SimpleGrantedAuthority(auth) } } +
            roleAuthorities
    }
}
