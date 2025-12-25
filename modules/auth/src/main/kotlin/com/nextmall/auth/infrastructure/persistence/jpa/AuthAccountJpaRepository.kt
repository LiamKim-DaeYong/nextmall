package com.nextmall.auth.infrastructure.persistence.jpa

import com.nextmall.auth.domain.account.AuthAccount
import com.nextmall.auth.domain.account.AuthProvider
import org.springframework.data.jpa.repository.JpaRepository

interface AuthAccountJpaRepository : JpaRepository<AuthAccount, Long> {
    fun existsByProviderAndProviderAccountId(
        provider: AuthProvider,
        providerAccountId: String,
    ): Boolean

    fun findByProviderAndProviderAccountId(
        provider: AuthProvider,
        providerAccountId: String,
    ): AuthAccount?
}
