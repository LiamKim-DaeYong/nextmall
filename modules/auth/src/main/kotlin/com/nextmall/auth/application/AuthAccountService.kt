package com.nextmall.auth.application

import com.nextmall.auth.domain.account.AuthAccount
import com.nextmall.auth.domain.account.AuthAccountCreatedEvent
import com.nextmall.auth.domain.account.AuthAccountStatus
import com.nextmall.auth.domain.account.AuthProvider
import com.nextmall.auth.domain.exception.DuplicateAuthAccountException
import com.nextmall.auth.infrastructure.persistence.jpa.AuthAccountJpaRepository
import com.nextmall.common.identifier.IdGenerator
import com.nextmall.common.kafka.producer.EventPublisher
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthAccountService(
    private val idGenerator: IdGenerator,
    private val authAccountJpaRepository: AuthAccountJpaRepository,
    private val passwordEncoder: PasswordEncoder,
    private val eventPublisher: EventPublisher,
) {
    /**
     * 인증 계정을 생성한다.
     *
     * - 사용자(userId)는 이미 생성된 상태여야 한다.
     * - 동일한 provider + providerAccountId 조합은 허용되지 않는다.
     */
    @Transactional
    fun createAccount(
        userId: Long,
        provider: AuthProvider,
        providerAccountId: String,
        password: String?,
    ): Long {
        validateAccountNotExists(provider, providerAccountId)

        val encodedPassword =
            password?.let { passwordEncoder.encode(it) }

        val account =
            AuthAccount(
                id = idGenerator.generate(),
                userId = userId,
                provider = provider,
                providerAccountId = providerAccountId,
                passwordHash = encodedPassword,
                status = AuthAccountStatus.ACTIVE,
            )

        authAccountJpaRepository.save(account)

        eventPublisher.publish(
            topic = "auth.events",
            key = account.userId.toString(),
            event =
                AuthAccountCreatedEvent(
                    accountId = account.id,
                    userId = account.userId,
                    provider = account.provider.name,
                ),
        )

        return account.id
    }

    private fun validateAccountNotExists(
        provider: AuthProvider,
        providerAccountId: String,
    ) {
        if (authAccountJpaRepository.existsByProviderAndProviderAccountId(provider, providerAccountId)) {
            throw DuplicateAuthAccountException()
        }
    }
}
