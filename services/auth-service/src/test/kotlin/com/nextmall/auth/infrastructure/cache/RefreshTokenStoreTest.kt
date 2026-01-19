package com.nextmall.auth.infrastructure.cache

import com.nextmall.common.testsupport.annotation.RedisIntegrationTest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.UUID

@RedisIntegrationTest
class RefreshTokenStoreTest(
    private val refreshTokenStore: RefreshTokenStore,
) : FunSpec({

        fun generateToken() = UUID.randomUUID().toString()

        test("save 후 findAuthAccountId로 조회 가능") {
            // given
            val refreshToken = generateToken()
            val accountId = 1L

            // when
            refreshTokenStore.save(refreshToken, accountId, 60L)

            // then
            refreshTokenStore.findAuthAccountId(refreshToken) shouldBe accountId

            // cleanup
            refreshTokenStore.delete(refreshToken)
        }

        test("존재하지 않는 토큰 조회 시 null 반환") {
            // given
            val refreshToken = generateToken()

            // when & then
            refreshTokenStore.findAuthAccountId(refreshToken) shouldBe null
        }

        test("delete 후 조회 시 null 반환") {
            // given
            val refreshToken = generateToken()
            refreshTokenStore.save(refreshToken, 1L, 60L)
            refreshTokenStore.findAuthAccountId(refreshToken) shouldBe 1L

            // when
            refreshTokenStore.delete(refreshToken)

            // then
            refreshTokenStore.findAuthAccountId(refreshToken) shouldBe null
        }

        test("TTL 만료 후 조회 시 null 반환") {
            // given
            val refreshToken = generateToken()
            refreshTokenStore.save(refreshToken, 1L, 1L) // 1초 TTL

            // when
            Thread.sleep(1500) // 1.5초 대기

            // then
            refreshTokenStore.findAuthAccountId(refreshToken) shouldBe null
        }
    })
