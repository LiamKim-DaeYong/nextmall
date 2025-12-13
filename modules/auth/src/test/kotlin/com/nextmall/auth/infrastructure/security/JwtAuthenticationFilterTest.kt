package com.nextmall.auth.infrastructure.security

import com.nextmall.auth.config.JwtProperties
import com.nextmall.auth.domain.model.TokenClaims
import com.nextmall.auth.port.output.token.TokenProvider
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.FilterChain
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import java.time.Instant

class JwtAuthenticationFilterTest :
    FunSpec({

        val tokenProvider = mockk<TokenProvider>()
        val jwtProperties =
            JwtProperties(
                secretKey = "dummy-secret-key-should-be-long-enough-1234567890",
                accessTokenExpiration = 10000,
                refreshTokenExpiration = 20000,
                tokenPrefix = "Bearer ",
            )

        val filter = JwtAuthenticationFilter(tokenProvider, jwtProperties)

        afterEach {
            SecurityContextHolder.clearContext()
        }

        test("Authorization 헤더가 없으면 인증 없이 체인만 호출된다") {
            val request = MockHttpServletRequest()
            val response = MockHttpServletResponse()
            val chain = mockk<FilterChain>(relaxed = true)

            filter.doFilter(request, response, chain)

            SecurityContextHolder.getContext().authentication.shouldBeNull()
            verify { chain.doFilter(request, response) }
        }

        test("Authorization prefix가 다르면 인증하지 않는다") {
            val request =
                MockHttpServletRequest().apply {
                    addHeader("Authorization", "Token abc")
                }
            val response = MockHttpServletResponse()
            val chain = mockk<FilterChain>(relaxed = true)

            filter.doFilter(request, response, chain)

            SecurityContextHolder.getContext().authentication.shouldBeNull()
            verify { chain.doFilter(request, response) }
        }

        test("parseAccessToken이 null을 반환하면 인증하지 않는다") {
            val request =
                MockHttpServletRequest().apply {
                    addHeader("Authorization", "Bearer invalid")
                }
            val response = MockHttpServletResponse()
            val chain = mockk<FilterChain>(relaxed = true)

            every { tokenProvider.parseAccessToken("Bearer invalid") } returns null

            filter.doFilter(request, response, chain)

            SecurityContextHolder.getContext().authentication.shouldBeNull()
            verify { chain.doFilter(request, response) }
        }

        test("만료된 토큰이면 인증하지 않는다") {
            val expiredClaims =
                TokenClaims(
                    userId = 1L,
                    roles = listOf("USER"),
                    expirationTime = Instant.now().minusSeconds(10),
                )

            every { tokenProvider.parseAccessToken("Bearer expired") } returns expiredClaims

            val request =
                MockHttpServletRequest().apply {
                    addHeader("Authorization", "Bearer expired")
                }
            val response = MockHttpServletResponse()
            val chain = mockk<FilterChain>(relaxed = true)

            filter.doFilter(request, response, chain)

            SecurityContextHolder.getContext().authentication.shouldBeNull()
            verify { chain.doFilter(request, response) }
        }

        test("유효한 토큰이면 SecurityContext에 인증 정보가 설정된다") {
            val claims =
                TokenClaims(
                    userId = 99L,
                    roles = listOf("ADMIN"),
                    expirationTime = Instant.now().plusSeconds(60),
                )

            every { tokenProvider.parseAccessToken("Bearer good") } returns claims

            val request =
                MockHttpServletRequest().apply {
                    addHeader("Authorization", "Bearer good")
                }
            val response = MockHttpServletResponse()
            val chain = mockk<FilterChain>(relaxed = true)

            filter.doFilter(request, response, chain)

            val auth = SecurityContextHolder.getContext().authentication
            auth.shouldBeInstanceOf<UsernamePasswordAuthenticationToken>()
            auth.principal shouldBe "99"
            auth.authorities.map { it.authority } shouldBe listOf("ROLE_ADMIN")

            verify { chain.doFilter(request, response) }
        }

        test("이미 인증이 있으면 기존 인증을 유지한다") {
            SecurityContextHolder.getContext().authentication =
                UsernamePasswordAuthenticationToken("existing", null, emptyList())

            val claims =
                TokenClaims(
                    userId = 123L,
                    roles = listOf("ADMIN"),
                    expirationTime = Instant.now().plusSeconds(60),
                )

            every { tokenProvider.parseAccessToken(any()) } returns claims

            val request =
                MockHttpServletRequest().apply {
                    addHeader("Authorization", "Bearer token")
                }
            val response = MockHttpServletResponse()
            val chain = mockk<FilterChain>(relaxed = true)

            filter.doFilter(request, response, chain)

            SecurityContextHolder.getContext().authentication?.principal shouldBe "existing"
            verify { chain.doFilter(request, response) }
        }

        test("roles가 비어 있으면 authorities도 비어 있다") {
            val claims =
                TokenClaims(
                    userId = 88L,
                    roles = emptyList(),
                    expirationTime = Instant.now().plusSeconds(60),
                )

            every { tokenProvider.parseAccessToken("Bearer token") } returns claims

            val request =
                MockHttpServletRequest().apply {
                    addHeader("Authorization", "Bearer token")
                }
            val response = MockHttpServletResponse()
            val chain = mockk<FilterChain>(relaxed = true)

            filter.doFilter(request, response, chain)

            val auth = SecurityContextHolder.getContext().authentication
            auth?.authorities shouldBe emptyList()
        }
    })
