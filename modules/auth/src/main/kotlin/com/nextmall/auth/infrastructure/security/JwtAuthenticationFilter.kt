package com.nextmall.auth.infrastructure.security

import com.nextmall.auth.config.JwtProperties
import com.nextmall.auth.domain.jwt.TokenProvider
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter
import java.util.Date

class JwtAuthenticationFilter(
    private val tokenProvider: TokenProvider,
    private val jwtProperties: JwtProperties,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
    ) {
        val header = request.getHeader("Authorization")

        if (header.isNullOrBlank() || !header.startsWith(jwtProperties.tokenPrefix)) {
            chain.doFilter(request, response)
            return
        }

        val claims =
            runCatching {
                tokenProvider.getClaims(header)
            }.getOrNull()

        if (claims == null || claims.expiration.before(Date())) {
            chain.doFilter(request, response)
            return
        }

        val userId = claims.subject

        if (SecurityContextHolder.getContext().authentication == null) {
            val auth =
                UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    emptyList(), // 현재는 권한 미사용(추후 확장)
                ).apply {
                    details = WebAuthenticationDetailsSource().buildDetails(request)
                }

            SecurityContextHolder.getContext().authentication = auth
        }

        chain.doFilter(request, response)
    }
}
