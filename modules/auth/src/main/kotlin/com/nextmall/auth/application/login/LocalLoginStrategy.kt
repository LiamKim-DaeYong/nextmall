package com.nextmall.auth.application.login

import com.nextmall.auth.application.exception.InvalidLoginException
import com.nextmall.auth.application.exception.TooManyLoginAttemptsException
import com.nextmall.auth.domain.account.AuthProvider
import com.nextmall.auth.infrastructure.cache.RateLimitRepository
import com.nextmall.auth.infrastructure.persistence.jpa.AuthAccountJpaRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class LocalLoginStrategy(
    private val authAccountJpaRepository: AuthAccountJpaRepository,
    private val passwordEncoder: PasswordEncoder,
    private val rateLimitRepository: RateLimitRepository,
) : LoginStrategy {
    override fun supports(provider: AuthProvider): Boolean =
        provider == AuthProvider.LOCAL

    override fun authenticate(
        identifier: String,
        credential: String?,
    ): Long {
        val email = identifier
        val password = credential ?: ""

        val identity = LoginIdentity.Companion.local(email)

        // 1) 실패 횟수 체크
        val failCount = rateLimitRepository.getFailCount(identity)
        if (failCount >= MAX_FAIL_COUNT) throw TooManyLoginAttemptsException()

        // 2) 계정 조회
        val account =
            authAccountJpaRepository
                .findByProviderAndProviderAccountId(AuthProvider.LOCAL, email)
                ?: run {
                    rateLimitRepository.increaseFailCount(identity)
                    throw InvalidLoginException()
                }

        // 3) 패스워드 비교
        val passwordHash =
            account.passwordHash ?: throw InvalidLoginException()

        if (!passwordEncoder.matches(password, passwordHash)) {
            rateLimitRepository.increaseFailCount(identity)
            throw InvalidLoginException()
        }

        // 4) 실패 카운트 초기화
        rateLimitRepository.resetFailCount(identity)

        return account.userId
    }

    companion object {
        private const val MAX_FAIL_COUNT = 5
    }
}
