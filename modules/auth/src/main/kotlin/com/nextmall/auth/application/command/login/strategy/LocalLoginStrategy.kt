package com.nextmall.auth.application.command.login.strategy

import com.nextmall.auth.application.command.login.LoginCommandParam
import com.nextmall.auth.port.output.account.AuthUserAccountQueryPort
import com.nextmall.auth.application.query.account.AuthUserAccountContext
import com.nextmall.auth.domain.exception.login.InvalidLoginException
import com.nextmall.auth.domain.exception.login.TooManyLoginAttemptsException
import com.nextmall.auth.domain.model.AuthProvider
import com.nextmall.auth.domain.model.LoginIdentity
import com.nextmall.auth.infrastructure.cache.RateLimitRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class LocalLoginStrategy(
    private val authUserAccountQueryPort: AuthUserAccountQueryPort,
    private val passwordEncoder: PasswordEncoder,
    private val rateLimitRepository: RateLimitRepository,
) : AuthLoginStrategy {
    override fun supports(provider: AuthProvider): Boolean =
        provider == AuthProvider.LOCAL

    override fun login(command: LoginCommandParam): AuthUserAccountContext {
        val email = command.principal
        val password = command.credential ?: ""

        val identity = LoginIdentity.local(email)

        // 1) 실패 횟수 체크
        val failCount = rateLimitRepository.getFailCount(identity)
        if (failCount >= MAX_FAIL_COUNT) throw TooManyLoginAttemptsException()

        // 2) 계정 조회
        val authUserAccountView =
            authUserAccountQueryPort.findByProviderAndAccountId(
                provider = AuthProvider.LOCAL,
                providerAccountId = email,
            ) ?: run {
                rateLimitRepository.increaseFailCount(identity)
                throw InvalidLoginException()
            }

        // 3) 패스워드 비교
        val passwordHash =
            authUserAccountView.passwordHash
                ?: throw InvalidLoginException()

        if (!passwordEncoder.matches(password, passwordHash)) {
            rateLimitRepository.increaseFailCount(identity)
            throw InvalidLoginException()
        }

        // 4) 실패 카운트 초기화
        rateLimitRepository.resetFailCount(identity)

        return authUserAccountView
    }

    companion object {
        private const val MAX_FAIL_COUNT = 5
    }
}
