package com.nextmall.auth.domain.jwt

import com.nextmall.auth.config.JwtProperties
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
            val token = provider.generateAccessToken("123")
            val claims = provider.getClaims(token)
            claims.subject shouldBe "123"
        }

        test("AccessToken과 RefreshToken을 생성한다") {
            val access = provider.generateAccessToken("123")
            val refresh = provider.generateRefreshToken("123")

            access.shouldContain(".")
            refresh.shouldContain(".")
        }

        test("만료된 토큰은 예외를 발생시킨다") {
            val shortProps = props.copy(accessTokenExpiration = 1)
            val shortProvider = TokenProvider(shortProps)
            val token = shortProvider.generateAccessToken("user-1")
            Thread.sleep(5)
            shouldThrow<ExpiredJwtException> {
                shortProvider.getClaims(token)
            }
        }
    })
