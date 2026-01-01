package com.nextmall.auth.infrastructure.security

import com.nextmall.auth.config.JwtProperties
import com.nextmall.auth.domain.token.TokenClaims
import com.nextmall.auth.port.output.token.TokenProvider
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TokenProviderTest :
    FunSpec({

        val props =
            JwtProperties(
                secretKey = "nm-dev-8f93A2dKq7sYb6tPxC1jE4rWvZ9uHgT!",
                accessTokenExpiration = 3600000,
                refreshTokenExpiration = 7200000,
                tokenPrefix = "Bearer ",
            )

        val provider: TokenProvider = JwtTokenProvider(props)

        test("AccessToken 생성 후 parseAccessToken으로 정상 파싱된다") {
            val token =
                provider.generateAccessToken(
                    userId = 123L,
                    roles = listOf("BUYER"),
                )

            val claims = provider.parseAccessToken(token)

            claims shouldBe
                TokenClaims(
                    authAccountId = 123L,
                    roles = listOf("BUYER"),
                    expirationTime = claims!!.expirationTime,
                )
        }

        test("RefreshToken 생성 후 parseRefreshToken으로 userId를 추출한다") {
            val refreshToken = provider.generateRefreshToken(1L)

            val userId = provider.parseRefreshToken(refreshToken)

            userId shouldBe 1L
        }

        test("만료된 access token은 parseAccessToken 시 null을 반환한다") {
            val shortProps = props.copy(accessTokenExpiration = 50)
            val shortProvider: TokenProvider = JwtTokenProvider(shortProps)

            val token =
                shortProvider.generateAccessToken(
                    userId = 1L,
                    roles = listOf("BUYER"),
                )

            Thread.sleep(200)

            val claims = shortProvider.parseAccessToken(token)

            claims shouldBe null
        }

        test("다른 secretKey로 서명된 토큰은 parseAccessToken 시 null을 반환한다") {
            val otherProvider =
                JwtTokenProvider(
                    props.copy(secretKey = "completely-other-secret-key-1234567890"),
                )

            val token =
                otherProvider.generateAccessToken(
                    authAccountId = 1L,
                    roles = listOf("BUYER"),
                )

            val parsed = provider.parseAccessToken(token)

            parsed shouldBe null
        }

        test("손상된 토큰 문자열은 parseAccessToken 시 null을 반환한다") {
            val malformedToken = "abc.def"

            val parsed = provider.parseAccessToken(malformedToken)

            parsed shouldBe null
        }

        test("AccessToken에는 roles 클레임이 포함된다") {
            val token =
                provider.generateAccessToken(
                    userId = 1L,
                    roles = listOf("BUYER"),
                )

            val claims = provider.parseAccessToken(token)!!

            claims.roles shouldBe listOf("BUYER")
        }
    })
