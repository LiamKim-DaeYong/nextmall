package com.nextmall.auth.domain.jwt

import com.nextmall.auth.config.JwtProperties
import com.nextmall.user.domain.entity.UserRole
import io.jsonwebtoken.ExpiredJwtException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class TokenProviderTest :
    FunSpec({

        val props =
            JwtProperties(
                secretKey = "nm-dev-8f93A2dKq7sYb6tPxC1jE4rWvZ9uHgT!",
                accessTokenExpiration = 3600000,
                refreshTokenExpiration = 7200000,
                tokenPrefix = "Bearer ",
            )
        val provider = TokenProvider(props)

        test("토큰 생성 후 클레임 파싱이 성공한다") {
            val token = provider.generateAccessToken("123", UserRole.BUYER.name)
            val claims = provider.getClaims(token)
            claims.subject shouldBe "123"
        }

        test("AccessToken과 RefreshToken을 생성한다") {
            val access = provider.generateAccessToken("123", UserRole.BUYER.name)
            val refresh = provider.generateRefreshToken("123")

            access.shouldContain(".")
            refresh.shouldContain(".")
        }

        test("만료된 토큰은 예외를 발생시킨다") {
            val shortProps = props.copy(accessTokenExpiration = 1)
            val shortProvider = TokenProvider(shortProps)
            val token = shortProvider.generateAccessToken("user-1", UserRole.BUYER.name)
            Thread.sleep(5)
            shouldThrow<ExpiredJwtException> {
                shortProvider.getClaims(token)
            }
        }

        test("다른 secretKey로 서명된 토큰은 검증에 실패한다") {
            val wrongProps =
                props.copy(secretKey = "completely-other-secret-key-1234567890")
            val wrongProvider = TokenProvider(wrongProps)

            val wrongToken = wrongProvider.generateAccessToken("user-1", UserRole.BUYER.name)

            shouldThrow<Exception> {
                provider.getClaims(wrongToken)
            }
        }

        test("손상된 토큰 문자열은 예외를 발생시킨다") {
            val malformedToken = "abc.def"

            shouldThrow<Exception> {
                provider.getClaims(malformedToken)
            }
        }

        test("정상 토큰에서 prefix(Bearer ) 제거 후 파싱이 정상 동작한다") {
            val token = provider.generateAccessToken("user-123", UserRole.BUYER.name)
            val bearerToken = props.tokenPrefix + token

            val parsed =
                provider.getClaims(
                    bearerToken.removePrefix(props.tokenPrefix),
                )

            parsed.subject shouldBe "user-123"
        }

        test("AccessToken에 roles 클레임이 포함된다") {
            val token = provider.generateAccessToken("123", UserRole.BUYER.name)
            val claims = provider.getClaims(token)

            claims.subject shouldBe "123"
            claims["roles"] shouldBe listOf("BUYER")
        }
    })
