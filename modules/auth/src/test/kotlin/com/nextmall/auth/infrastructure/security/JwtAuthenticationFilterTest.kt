package com.nextmall.auth.infrastructure.security

import com.nextmall.auth.config.JwtProperties
import com.nextmall.auth.domain.jwt.TokenProvider
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
import java.util.Date

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

        test("Authorization 헤더가 없으면 체인만 호출하고 인증은 설정되지 않는다") {
            val request = MockHttpServletRequest()
            val response = MockHttpServletResponse()
            val chain = mockk<FilterChain>(relaxed = true)

            filter.doFilter(request, response, chain)

            SecurityContextHolder.getContext().authentication.shouldBeNull()
            verify { chain.doFilter(request, response) }
        }

        test("Authorization prefix가 일치하지 않으면 필터를 통과하고 인증하지 않는다") {
            val request =
                MockHttpServletRequest().apply {
                    addHeader("Authorization", "Token something")
                }
            val response = MockHttpServletResponse()
            val chain = mockk<FilterChain>(relaxed = true)

            filter.doFilter(request, response, chain)

            SecurityContextHolder.getContext().authentication.shouldBeNull()
            verify { chain.doFilter(request, response) }
        }

        test("claims 파싱 실패하면 체인만 수행된다") {
            val request =
                MockHttpServletRequest().apply {
                    addHeader("Authorization", "Bearer invalid-token")
                }
            val response = MockHttpServletResponse()
            val chain = mockk<FilterChain>(relaxed = true)

            every { tokenProvider.getClaims(any()) } throws RuntimeException("invalid token")

            filter.doFilter(request, response, chain)

            SecurityContextHolder.getContext().authentication.shouldBeNull()
            verify { chain.doFilter(request, response) }
        }

        test("토큰이 만료되었으면 인증하지 않고 통과한다") {
            val expiredClaims = mockk<io.jsonwebtoken.Claims>()
            every { expiredClaims.expiration } returns Date(System.currentTimeMillis() - 1000)
            every { expiredClaims.subject } returns "1"

            val request =
                MockHttpServletRequest().apply {
                    addHeader("Authorization", "Bearer valid-token")
                }
            val response = MockHttpServletResponse()
            val chain = mockk<FilterChain>(relaxed = true)

            every { tokenProvider.getClaims("Bearer valid-token") } returns expiredClaims

            filter.doFilter(request, response, chain)

            SecurityContextHolder.getContext().authentication.shouldBeNull()
            verify { chain.doFilter(request, response) }
        }

        test("유효한 토큰이면 SecurityContext에 인증 정보가 설정된다") {
            val validClaims = mockk<io.jsonwebtoken.Claims>()

            every { validClaims.expiration } returns Date(System.currentTimeMillis() + 10000)
            every { validClaims.subject } returns "99"

            every { tokenProvider.getClaims("Bearer good-token") } returns validClaims

            val request =
                MockHttpServletRequest().apply {
                    addHeader("Authorization", "Bearer good-token")
                }
            val response = MockHttpServletResponse()
            val chain = mockk<FilterChain>(relaxed = true)

            filter.doFilter(request, response, chain)

            val auth = SecurityContextHolder.getContext().authentication
            auth.shouldBeInstanceOf<UsernamePasswordAuthenticationToken>()
            auth.principal shouldBe "99"

            verify { chain.doFilter(request, response) }
        }

        test("이미 SecurityContext에 인증이 있으면 새로 설정하지 않는다") {
            SecurityContextHolder.getContext().authentication =
                UsernamePasswordAuthenticationToken("existing", null, emptyList())

            val validClaims = mockk<io.jsonwebtoken.Claims>()
            every { validClaims.expiration } returns Date(System.currentTimeMillis() + 10000)
            every { validClaims.subject } returns "123"

            every { tokenProvider.getClaims(any()) } returns validClaims

            val request =
                MockHttpServletRequest().apply {
                    addHeader("Authorization", "Bearer token")
                }
            val response = MockHttpServletResponse()
            val chain = mockk<FilterChain>(relaxed = true)

            filter.doFilter(request, response, chain)

            SecurityContextHolder.getContext().authentication!!.principal shouldBe "existing"

            verify { chain.doFilter(request, response) }
        }
    })
