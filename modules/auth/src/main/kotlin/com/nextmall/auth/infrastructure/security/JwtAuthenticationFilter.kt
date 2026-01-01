package com.nextmall.auth.infrastructure.security

import com.nextmall.auth.config.JwtProperties
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter
import java.time.Instant

class JwtAuthenticationFilter(
    private val tokenProvider: JwtTokenProvider,
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
                tokenProvider.parseAccessToken(header)
            }.getOrNull()

        if (claims == null || claims.expirationTime.isBefore(Instant.now())) {
            chain.doFilter(request, response)
            return
        }

        val userId = claims.authAccountId.toString()
        val authorities =
            claims.roles
                .map { SimpleGrantedAuthority("ROLE_$it") }

        if (SecurityContextHolder.getContext().authentication == null) {
            val authentication =
                UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    authorities,
                ).apply {
                    details = WebAuthenticationDetailsSource().buildDetails(request)
                }

            SecurityContextHolder.getContext().authentication = authentication
        }

        chain.doFilter(request, response)
    }
}
